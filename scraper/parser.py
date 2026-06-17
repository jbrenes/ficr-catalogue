"""Transform raw FICK JSON responses into PagaiaCronos domain objects."""
import hashlib
import re


# ── Helpers ────────────────────────────────────────────────────────────────────

def _fick_id(surname: str, name: str, birth: str, team_cod: str) -> str:
    """Stable athlete identifier (FICK has no persistent IDs across race files)."""
    key = f"{surname.upper()}|{name.upper()}|{birth}|{team_cod}"
    return hashlib.md5(key.encode()).hexdigest()


def _parse_time_ms(raw: str | None) -> int | None:
    """Convert FICK time string '1\'53.75' or '53.75' to milliseconds."""
    if not raw or raw.strip() in ("NP", "NA", "SQ", "---", ""):
        return None
    raw = raw.strip()
    m = re.match(r"^(\d+)'(\d+)\.(\d+)$", raw)
    if m:
        mins, secs, cents = int(m[1]), int(m[2]), int(m[3].ljust(2, "0")[:2])
        return (mins * 60 + secs) * 1000 + cents * 10
    m = re.match(r"^(\d+)\.(\d+)$", raw)
    if m:
        secs, cents = int(m[1]), int(m[2].ljust(2, "0")[:2])
        return secs * 1000 + cents * 10
    return None


def _parse_date(ficr_date: str) -> str | None:
    """Convert 'DD/MM/YYYY' → 'YYYY-MM-DD'."""
    parts = ficr_date.strip().split("/")
    if len(parts) != 3:
        return None
    return f"{parts[2]}-{parts[1].zfill(2)}-{parts[0].zfill(2)}"


def _derive_boat_class(competition_name: str) -> str | None:
    m = re.search(r"(K\d|C\d)", competition_name, re.IGNORECASE)
    return m.group(1).upper() if m else None


def _derive_distance(competition_name: str) -> int | None:
    m = re.search(r"(\d+)\s*m", competition_name, re.IGNORECASE)
    return int(m.group(1)) if m else None


def _derive_gender(category_code: str) -> str:
    if not category_code:
        return "M"
    last = category_code[-1].upper()
    return "F" if last == "F" else "M"


# ── Parsers ────────────────────────────────────────────────────────────────────

def parse_race_result(raw: dict, manifest_code: str, tipologia: str,
                      round_cod: str, heat_cod: str) -> dict | None:
    """Parse one race result JSON blob into a (race, results, athletes, links) dict."""
    data = raw.get("data") or raw.get("Data")
    if not data:
        return None

    comp = data.get("Competition", {})
    cat  = data.get("Category", {})
    rnd  = data.get("Round", {})
    evt  = data.get("Event", {})

    competition_name = comp.get("Ita", "")
    boat_class = _derive_boat_class(competition_name)
    distance_m = _derive_distance(competition_name)
    gender     = _derive_gender(cat.get("Cod", ""))
    date_iso   = _parse_date(evt.get("Date", ""))

    fick_race_id = f"{manifest_code}_{tipologia}_{round_cod}_{heat_cod}"
   
    race = {
        "id": fick_race_id,
        "fick_race_id": fick_race_id,
        "fick_event_id": manifest_code,
        "distance_m": distance_m,
        "boat_class": boat_class,
        "gender": gender,
        "category_code": cat.get("Cod"),
        "category_name": cat.get("Ita"),
        "round_name": rnd.get("Ita"),
        "round_code": rnd.get("Cod"),
        "heat_number": int(heat_cod) if heat_cod.isdigit() else 1,
    }

    event = {
        "id": manifest_code,
        "fick_event_id": manifest_code,
        "date": date_iso or "",
        "name": evt.get("Place")
       
    }

    athletes = {}
    results  = []
    links    = []
    categories = set()  # for debugging: track unique category codes encountered
    distances = set()
    aCategory = {}
    aCategory[cat.get("Cod")] = {
        "category_code": cat.get("Cod"),
        "category_name": cat.get("Ita")
    }
    distances.add(distance_m)
    clubs = {}   # for debugging: track unique distances encountered
    for row in data.get("data", []):
        categories.add(row.get("PlaCls", ""))
        dns = str(row.get("PlaCls", "")).upper() == "NP" or str(row.get("MemPrest", "")).upper() == "NP"
        dnf = str(row.get("MemPrest", "")).upper() == "NA"
        dsq = str(row.get("MemPrest", "")).upper() == "SQ"
        rank_raw = row.get("PlaCls", "")
        rank = int(rank_raw) if str(rank_raw).isdigit() else None
        time_ms = _parse_time_ms(row.get("MemPrest"))
        gap_ms  = _parse_time_ms(row.get("Gap")) if row.get("Gap") else None

        # Build crew list
        players = row.get("Players")
        if players:
            crew = players
        else:
            crew = [row]  # solo boat: athlete fields are at top level

        fick_result_id = f"{fick_race_id}_{row.get('PlaCod', '')}"

        result = {
            "id": fick_result_id,
            "fick_result_id": fick_result_id,
            "fick_race_id": fick_race_id,
            "lane": _safe_int(row.get("PlaLane")),
            "rank": rank,
            "time_ms": time_ms,
            "gap_ms": gap_ms,
            "points": _safe_int(row.get("MemPoint")),
            "dns": dns,
            "dnf": dnf,
            "dsq": dsq,
        }
        results.append(result)

        for seat_idx, player in enumerate(crew):
            surname   = player.get("PlaSurname", "")
            name      = player.get("PlaName", "")
            birth     = player.get("PlaBirth", "")
            team_cod  = player.get("PlaTeamCod", row.get("PlaTeamCod", ""))
            team_name = row.get("TeamDescrIta", "")
            fid       = _fick_id(surname, name, birth, team_cod)

            athletes[fid] = {
                "id": fid,
                "first_name": name.title(),
                "last_name": surname.upper(),
                "birth_date": _parse_date(birth),
                "club": team_name or None,
                "club_code": team_cod or None,
                "nationality": player.get("PlaNat", "ITA"),
            }
            if team_cod != None:
                clubs[team_cod] = {
                    "club_code": team_cod,  
                    "club": team_name
                }
            links.append({
                "fick_result_id": fick_result_id,
                "fick_athlete_id": fid,
                "seat_order": seat_idx,
            })

    return {
        "event": event,
        "race": race,
        "athletes": athletes,
        "results": results,
        "race_athletes": links,
        "clubs": clubs,
        "distances": distances,
        "categories": aCategory,
    }


def _safe_int(val) -> int | None:
    try:
        return int(val)
    except (TypeError, ValueError):
        return None
