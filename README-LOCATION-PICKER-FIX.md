# ASMJ Marketplace — State, City and PIN Code Fix

## What changed
- The listing form now passes the selected state code into `LocationPicker`; this fixes a mismatch where the UI selected a state by code but attempted to filter cities using the state name.
- The shared location picker includes an offline fallback for Indian states/UTs and common cities, including Bhawanipatna and Kalahandi, Odisha.
- API-configured cities are merged with the fallback list instead of being the only source of options.
- PIN code is editable and limited to six digits. It is not guessed from the city because cities can contain many PIN-code areas.
- An “Other city / locality” option allows users to enter a city not listed.
- The same component is shared by listing creation, service booking and Printout home delivery forms.

## Build and test
From `frontend` run:

```bash
npm ci
npm run build
```

Then test:
1. Create a listing and select Odisha → Bhawanipatna; verify the values remain selected when the form rerenders.
2. Select a state and use “Other city / locality”; enter a city name.
3. Enter fewer than six digits and confirm required forms prevent submission.
4. Check service booking and Printout home delivery use the same picker.
5. Confirm the chosen state, city and PIN are saved and shown correctly after reopening a listing.

The PIN code must be entered for the specific delivery/locality; it is not automatically inferred from city alone.
