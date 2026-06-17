"""HTTP client for FICK (Federazione Italiana Canoa e Kayak) APIs."""
import re
import time
import requests

BASE_VARS = "https://apimanvarie.ficr.it/VAR/mpcache-30/get"
BASE_CAV  = "https://apicanoavelocita.ficr.it/CAV"

SESSION = requests.Session()
SESSION.headers.update({"User-Agent": "PagaiaCronos-Scraper/1.0"})


def _get(url: str, retries: int = 3) -> dict:
    for attempt in range(retries):
        try:
            r = SESSION.get(url, timeout=30)
            r.raise_for_status()
            return r.json()
        except Exception as e:
            if attempt == retries - 1:
                raise
            time.sleep(2 ** attempt)
    return {}


def get_season_schedule(year: int) -> list[dict]:
    """Returns a list of competition schedule entries for the given season year."""
    data = _get(f"{BASE_VARS}/schedule/{year}/*/19")
    # Response: {"code":200,"data":[...]}
    entries = data.get("data", [])
    results = []
    for entry in entries:
        codicePub = entry.get("CodicePub", "")
        results.append({
                "ManifestCode": re.sub(r'[^\x00-\x7f]',r'', codicePub).strip(),
                "ManifestYear": str(year),
                "Place": entry.get("Place", "N/A"),
                "Date": entry.get("Data", "")
                
            })
      
            
    return results


def get_competition_program(cod_pub: str) -> dict:
    """Returns the full program for a competition code (CodPub / ManifestCode)."""
    """ Example GET https://apicanoavelocita.ficr.it/CAV/mpcache-30/get/programdate/CanoaDigaNicolettiEN16072022_69"""
    data = _get(f"{BASE_CAV}/mpcache-30/get/programdate/{cod_pub}")
    entries = data.get("data", [])
    races = []
    for entry in entries:
        for sub_entry in entry.get("e", []):
            races.append({
                "codS": entry.get("codS", ""),
                "c0": sub_entry.get("c0", ""),
                "c1": sub_entry.get("c1", ""),
                "c2": sub_entry.get("c2", "")[-2:] if sub_entry.get("c2") else "",
                "c3": sub_entry.get("c3", "")
            })
    return races


def get_race_result(cod_pub: str, cod_s: str, cat: str,
                    c1: str, c2: str, c3: str) -> dict:
    """Returns the result JSON for a specific race within a competition.

    Arguments match the FICK URL pattern:
        result/{CodPub}/{codS}/{cat}/{c1}/{c2[-2:]}/{c3}
    """
    return _get(f"{BASE_CAV}/mpcache-10/get/result/{cod_pub}/{cod_s}/{cat}/{c1}/{c2}/{c3}")
