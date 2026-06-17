"""Aggregate parsed race data into the PagaiaCronos sync JSON format."""
import json
from datetime import datetime, timezone


def build_payload(parsed_races: list[dict], existing_clubs: dict[str, dict], existing_distances: list[str], existing_categories: dict[str, dict], 
                  existing_events:dict[str,dict], existing_athletes: dict[str, dict],
                  existing_races: list[dict],
                  existing_results: list[dict],
                  existing_results_athletes: list[dict]) -> dict:
    """Merge all parsed race chunks into a single versioned sync payload.

    Args:
        parsed_races: list of dicts from parser.parse_race_result()

    Returns:
        A dict matching the SyncPayload JSON schema.
    """
    athletes: dict[str, dict] = {}
    events:   dict[str, dict] = {}
    races:    list[dict] = []
    results:  list[dict] = []
    links:    list[dict] = []
   
    
    

    for chunk in parsed_races:
        if chunk is None:
            continue

        
        

        # Deduplicate events by fick_event_id
        evt = chunk["event"]
        events.setdefault(evt["fick_event_id"], evt)

        races.append(chunk["race"])
        results.extend(chunk["results"])
        links.extend(chunk["race_athletes"])
        existing_clubs.update(chunk.get("clubs", {}))
        existing_athletes.update(chunk.get("athletes", {}))
        existing_categories.update(chunk.get("categories", {}))
        existing_distances.extend(chunk.get("distances", []))
    existing_results.extend(results)
    existing_results_athletes.extend(links)
    existing_races.extend(races)    
    existing_events.update(events)
    return {
        "version": datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
        "athletes": list(existing_athletes.values()),
        "events": list(existing_events.values()),
        "races": existing_races,
        "results": existing_results,
        "results_athletes": existing_results_athletes,
        "clubs": list(existing_clubs.values()),
        "distances": list(set(existing_distances)),
        "categories": list(existing_categories.values())
    }


def write_payload(payload: dict, out_path: str) -> None:
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, indent=2)
    print(f"Written {out_path} — "
          f"{len(payload['athletes'])} athletes, "
          f"{len(payload['events'])} events, "
          f"{len(payload['races'])} races, "
          f"{len(payload['results'])} results, "
          f"{len(payload['categories'])} categories, "
          f"{len(payload['distances'])} distances, "
          f"{len(payload['clubs'])} clubs"  
        )

