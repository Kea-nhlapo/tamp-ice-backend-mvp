# Two-Day Work Plan

## Priority rule

Complete and verify the working end-to-end business journey before spending time on optional infrastructure improvements.

## Day 1

- Scaffold the Spring Boot Maven application with Java 21.
- Configure the development database.
- Implement entities and repositories.
- Implement registration, login, JWT handling and RBAC.
- Add user profiles and compliance metadata.
- Implement load operations.
- Implement truck operations.
- Add basic Swagger configuration.
- Implement transparent matching rules.
- Add initial unit tests.

**Day 1 outcome:** authenticated users can create the core postings and obtain explainable match candidates through Swagger.

## Day 2

- Implement match acceptance and rejection.
- Generate digital receipts.
- Persist audit events.
- Add simulated tracking.
- Add ratings and disputes.
- Implement administrator endpoints and metrics.
- Add global error handling.
- Complete the remaining priority tests.
- Add synthetic seed data.
- Add GitHub Actions.
- Update documentation using actual commands and results.
- Rehearse the demonstration.

**Day 2 outcome:** the primary journey works end to end, automated checks run, documentation reflects reality and the demonstration has been rehearsed.

If time is constrained, protect registration, posting, matching, acceptance, receipt, tracking and administrator evidence first. Optional polish follows only after these are reliable.
