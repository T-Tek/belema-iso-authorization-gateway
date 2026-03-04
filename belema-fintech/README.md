# Belema ISO 8583 Authorization Gateway

A TCP-based ISO 8583 payment authorization service built with Java, Spring Boot, Netty and j8583.

---

## How to Run

Make sure you have Java 17+ and Maven installed.

**Navigate into the project:**
```bash
cd belema-fintech
```

**Run the application:**
```bash
mvn spring-boot:run
```

The server starts on port **8583** by default.

**Run tests:**
```bash
mvn test
```

---

## Sample Request / Response

### Request (MTI 0100)

| Field | Name | Value |
|-------|------|-------|
| 2 | PAN | 4111111111111111 |
| 3 | Processing Code | 000000 |
| 4 | Amount | 000000005000 (NGN 50.00) |
| 7 | Transmission DateTime | 0228143000 |
| 11 | STAN | 000001 |
| 12 | Local Transaction Time | 143000 |
| 13 | Local Transaction Date | 0228 |
| 14 | Expiration Date | 2612 |
| 18 | Merchant Type | 5411 |
| 22 | POS Entry Mode | 051 |
| 25 | POS Condition Code | 00 |
| 28 | Transaction Fee | 00000000 |
| 32 | Acquiring Institution | 000001 |
| 37 | RRN | REF000000001 |
| 41 | Terminal ID | TERM0001 |
| 42 | Merchant ID | MERCHANT0000001 |
| 43 | Card Acceptor Name | TEST MERCHANT LAGOS NG |
| 49 | Currency Code | 566 |

### Response (MTI 0110)

| Field | Name | Value |
|-------|------|-------|
| 2 | PAN | echoed from request |
| 3 | Processing Code | echoed from request |
| 4 | Amount | echoed from request |
| 37 | RRN | echoed from request |
| 39 | Response Code | 00 = Approved, 05 = Declined |
| 49 | Currency Code | echoed from request |

**Approval logic:**
- Amount <= NGN 100.00 → Response Code `00` (Approved)
- Amount > NGN 100.00 → Response Code `05` (Declined)

---

## Assumptions

- Field 4 (Amount) is transmitted in cents, e.g. `000000005000` = NGN 50.00
- Messages are framed with a 2-byte big-endian length prefix over TCP
- ASCII encoding is used, binary bitmap mode is disabled
- Fields 123 and 128 are excluded — Field 128 requires a MAC (cryptographic shared secret) between systems which is not available in a standalone simulation, and Field 123 is proprietary POS data that varies by processor
- No TLS, plain TCP is used for simplicity
- Currency code 566 (NGN) is used across all test scenarios
- PANs are masked in logs for basic PCI-DSS awareness