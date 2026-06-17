#!/usr/bin/env python3
"""
PagaiaCronos scraper — fetches FICK canoe sprint results and outputs a
versioned JSON file ready for sync into the mobile app.

Usage:
    python scraper.py --season 2024 --out-file ficr-2024.json
    python scraper.py --season 2025 --out-file ficr-2025.json --incremental ficr-2025.json
"""
import argparse
import json
from operator import attrgetter
import sys
import time
from pathlib import Path

import ficr_client as client
import parser as ficr_parser
import exporter


def load_existing_event_ids(path: str) -> set[str]:
    """Return fick_event_ids already present in an existing payload file."""
    try:
        with open(path) as f:
            data = json.load(f)
        return {e["fick_event_id"] for e in data.get("events", [])}
    except (FileNotFoundError, json.JSONDecodeError):
        return set()


def scrape_season(year: int, existing_ids: set[str], max_events: int = -1) -> list[dict]:
    print(f"Fetching season schedule for {year}…")
    schedule = client.get_season_schedule(year)
    print(f"  Found {len(schedule)} event entries")

    # Group by ManifestCode (competition event)
    events: dict[str, list[dict]] = {}
    processed_events = 0
    for entry in schedule:
        cod = entry.get("ManifestCode", "")
        if cod:
            races = client.get_competition_program(cod) 
            events.setdefault(cod, races)

    parsed = []
    for cod_pub, races in events.items():
        if cod_pub in existing_ids:
            print(f"  Skipping {cod_pub} (already in payload)")
            continue

        print(f"  Processing event {cod_pub} ({len(races)} races)…")
        for race in races:
            # Build the race result URL parameters from the schedule entry
            codS = race.get("codS", "")
            c0 = race.get("c0", "")
            c1  = race.get("c1", "")
            c2  = race.get("c2", "")[-2:] if race.get("c2") else ""
            c3  = race.get("c3", "")
            tipologia = race.get("c1", "")

          
          

            try:
                raw = client.get_race_result(cod_pub, codS, c0, c1, c2, c3)
                chunk = ficr_parser.parse_race_result(
                    raw,
                    manifest_code=cod_pub,
                    tipologia=tipologia,
                    round_cod=c2,
                    heat_cod=c3
                )
                if chunk:
                    parsed.append(chunk)
                time.sleep(0.3)  # polite rate-limiting
            except Exception as e:
                print(f"    WARNING: skipped race {cod_pub}/{cat}/{c1}/{c2}/{c3}: {e}")
        processed_events += 1
        if 0 < max_events <= processed_events:
            print(f"  Reached max events limit ({max_events}), stopping early")
            break   

    return parsed


def main():
    ap = argparse.ArgumentParser(description="PagaiaCronos FICK scraper")
    ap.add_argument("--season", type=int, required=True, help="Competition year (e.g. 2025)")
    ap.add_argument("--out-file", required=True, help="Output JSON file path")
    ap.add_argument(
        "--incremental",
        metavar="EXISTING_FILE",
        help="Skip events already present in this existing payload file"
    )
    ap.add_argument(
        "--max",
        type=int,
        help="Limit the mount of events parsed for debugging (default: all events)",
    )
    args = ap.parse_args()
    existing_clubs = {}
    existing_distances = []
    existing_categories = {}
    existing_events = {}
    existing_athletes = {}
    existing_results = []
    existing_results_athletes = []
    existing_races = []

    existing_ids: set[str] = set()
    if args.incremental:
        existing_ids = load_existing_event_ids(args.incremental)
        print(f"Incremental mode: {len(existing_ids)} existing events will be skipped")

    if args.max:
        print(f"Limiting to {args.max} events for debugging")
        
        parsed = scrape_season(args.season, existing_ids,args.max)

    else:
        parsed = scrape_season(args.season, existing_ids, -1)

    if args.incremental and Path(args.incremental).exists():
        # Merge with existing payload
        with open(args.incremental) as f:
            existing = json.load(f)
            existing_athletes = {athlete['id']: athlete for athlete in existing.get("athletes", [])}
            existing_clubs= {club['club_code']: club for club in existing.get("clubs", [])}
            existing_distances = existing.get("distances", [])
            existing_categories = {cat['category_code']: cat for cat in existing.get("categories", [])}
            existing_events = {event['fick_event_id']: event for event in existing.get("events", [])}
            existing_results = existing.get("results", [])
            existing_results_athletes = existing.get("results_athletes", [])
            existing_races = existing.get("races", []) 
        # We rebuild from scratch, so just concatenate existing data as pre-parsed chunks
        # (simpler: just re-export the merged data by converting existing entries back)
        # For simplicity, write new payload standalone and let user merge manually.
        print("NOTE: Incremental merge writes only new data. Manually merge with existing file if needed.")

    payload = exporter.build_payload(parsed, 
                                     existing_clubs=existing_clubs,
                                       existing_distances=existing_distances,
                                         existing_categories=existing_categories, 
                                         existing_events=existing_events, 
                                         existing_athletes=existing_athletes,
                                         existing_races = existing_races,
                                         existing_results=existing_results,
                                         existing_results_athletes=existing_results_athletes)

    if not parsed:
        print("No new data found. Exiting without writing file.")
        sys.exit(0)

    exporter.write_payload(payload, args.out_file)


if __name__ == "__main__":
    main()
