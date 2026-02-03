import json
import sys

def handle(req):
    """
    FaaS Logic: Estimator de cheltuieli în funcție de specific și grup.
    Input JSON: {"cuisine": "italian", "group_size": 4, "is_weekend": true}
    """
    try:
        if not req:
            return json.dumps({"error": "Te rog introdu datele pentru estimare."})

        data = json.loads(req)
        cuisine = data.get("cuisine", "general").lower()
        group_size = int(data.get("group_size", 1))
        is_weekend = data.get("is_weekend", False)

        avg_prices = {
            "italian": 35,
            "japanese": 55,
            "romanian": 25,
            "fast_food": 15,
            "fine_dining": 120,
            "general": 30
        }

        price_per_person = avg_prices.get(cuisine, 30)

        if is_weekend:
            price_per_person *= 1.15

        estimated_total = round(price_per_person * group_size, 2)

        result = {
            "cuisine": cuisine,
            "groupSize": group_size,
            "estimatedTotal": estimated_total,
            "currency": "EUR",
            "breakdown": {
                "perPerson": round(price_per_person, 2),
                "serviceTax": "10% inclus"
            },
            "engine": "OpenFaaS-Watchdog-Python"
        }

        return json.dumps(result)

    except Exception as e:
        return json.dumps({"error": f"Eroare la calcul: {str(e)}"})

if __name__ == "__main__":
    input_data = sys.stdin.read()
    print(handle(input_data))