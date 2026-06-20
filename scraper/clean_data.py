#!/usr/bin/env python3
"""
One-off cleanup utility for existing PagaiaCronos sync JSON files: removes
null/empty categories and null/0 distances left over from before the
parser guarded against them.

Usage:
    python clean_data.py ficrdb.json
    python clean_data.py ficrdb.json --out ficrdb.clean.json
"""
import argparse
import json


def clean_payload(payload: dict) -> dict:
    categories = [
        c for c in payload.get("categories", [])
        if c.get("category_code")
    ]
    distances = [d for d in payload.get("distances", []) if d]

    removed_categories = len(payload.get("categories", [])) - len(categories)
    removed_distances = len(payload.get("distances", [])) - len(distances)
    print(f"Removed {removed_categories} null/empty categories, "
          f"{removed_distances} null/0 distances")

    payload["categories"] = categories
    payload["distances"] = distances
    return payload


def main():
    ap = argparse.ArgumentParser(description="Clean a PagaiaCronos sync JSON file")
    ap.add_argument("in_file", help="Path to the JSON file to clean")
    ap.add_argument("--out", help="Output path (defaults to overwriting in_file)")
    args = ap.parse_args()

    with open(args.in_file, encoding="utf-8") as f:
        payload = json.load(f)

    cleaned = clean_payload(payload)

    out_path = args.out or args.in_file
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(cleaned, f, ensure_ascii=False, indent=2)
    print(f"Written {out_path}")


if __name__ == "__main__":
    main()
