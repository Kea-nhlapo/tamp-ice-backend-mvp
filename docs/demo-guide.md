# Demonstration Guide

Start the application with `.\mvnw.cmd spring-boot:run`, then open `http://localhost:8080/swagger-ui/index.html`.

The synthetic users all use `Password123!`: `owner@tamp.test`, `transporter@tamp.test` and `admin@tamp.test`.

1. Open Swagger and call `POST /api/auth/login` as the Freight Owner.
2. Copy the returned token into Swagger's **Authorize** dialog.
3. Create a cargo load or use synthetic load ID `1`.
4. Log in as the Transporter and authorise with its token.
5. Create an available truck or use synthetic truck ID `1`.
6. Log in as the Freight Owner again and call `POST /api/loads/1/matches`.
7. Explain why the truck qualifies using the returned reasons.
8. Accept the match.
9. Retrieve the digital receipt.
10. Advance the mock tracking status.
11. Complete the trip and submit a rating.
12. Log in as the Administrator and replace the authorised token.
13. View users, compliance metadata, dispute information, audit events and metrics.

Use synthetic demonstration data only. The H2 data resets whenever the application restarts.
