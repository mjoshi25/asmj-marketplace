# Rental driver odometer validation

- Driver trip form now labels the reading by workflow stage: starting, outbound ending, return starting, or final odometer reading.
- The form explains that drivers must enter the full dashboard odometer value, not the distance travelled.
- The UI blocks blank, non-numeric, zero/negative, and lower-than-previous readings before sending a trip action.
- Backend KmRequest accepts a nullable reading so an omitted JSON value cannot silently become primitive zero.
- Backend start, outbound-end, return-start, and end actions require a positive odometer reading; ending readings cannot be below the corresponding start reading.
- The trip cannot transition to JOURNEY_COMPLETED or FARE_FINALIZED unless a valid final reading is supplied. Actual KM and fare remain server-calculated.

Build status: ZIP integrity checked. Maven/Vite production builds were not run in this environment.
