
# TODO:

1. Test out queries on the large OEIS db for debug and benchmark purposes
    - Use VisualVM or similar to profile
    - MongoCodec: 16%
    - evalMFProperty: 70%
        - Decoding is probably the slow part

2. Get the mongo property-provider to only encode the bell-table values that are needed, this is a huge time-sink

3. Get the mongo query system to only retrieve what is needed, using projections

4. Finish parser
    - Use built-in scala parser, not custom parser

5. Clean up entire codebase
    - Proper exception handling, for such as mongodb connections
    - Clean up handling of f(1)
    - Increase test coverage, and extensively test code
    - Fix warnings
    - Make dbmath use datatypes

6. Create a web-API

7. Deploy on Heroku

8. Create a webpage

9. Integrate with zeta-types-scala main project, add many mathematical tools to query system

10. Create an LMFDB downloader

11. Work on test-cases and making them as simple as possible

12. Expand project
    - Include all essential MFProperties
    - Create compound properties related to the various datatypes
