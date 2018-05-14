
# TODO:
1. Speed up belltable-generation in OEIS converter
    - Implement better prime sieve [Done for now]
    - Debug the other time-sinks [IndexedSeq added, seems fast now]

2. Get the OEIS downloader working
    - There appear to be memory leaks

3. Test out queries on the large OEIS db for debug and benchmark purposes

4. Get the mongo property-provider to only encode the bell-table values that are needed, this is a huge time-sink

5. Get the mongo query system to only retrieve what is needed, using projections

6. Finish parser

7. Clean up entire codebase

8. Create a web-API

9. Deploy on Heroku

10. Create a webpage

11. Integrate with zeta-types-scala main project, add many mathematical tools to query system

12. Create an LMFDB downloader

13. Work on test-cases and making them as simple as possible
