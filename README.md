# GutenbergDb — Publishing House Database System

Java + JDBC console application for CSC 540 Project Report 3.  
Database: MariaDB hosted at `classdb2.csc.ncsu.edu:3306`.

## Requirements

- Java 17+
- `mariadb-java-client-3.5.7.jar` (included in repo)
- Access to NCSU campus network or EOS SSH (`remote.eos.ncsu.edu`)

## How to Run

### On the NCSU EOS SSH server (recommended)

```bash
ssh <unity_id>@remote.eos.ncsu.edu
git clone https://github.com/adityapai18/DBMS_project.git
cd DBMS_project
mkdir -p out
javac -cp "mariadb-java-client-3.5.7.jar" -d out $(find src -name "*.java")
java -cp "out:mariadb-java-client-3.5.7.jar" main.MainApp
```

### On your local machine (requires VPN)

Connect to the NCSU VPN first, then:

```bash
mkdir -p out
javac -cp "mariadb-java-client-3.5.7.jar" -d out $(find src -name "*.java")
java -cp "out:mariadb-java-client-3.5.7.jar" main.MainApp
```

## Menu Structure

```
Main Menu
├── 1. Publishing
│   ├── 1. Enter / update publication
│   ├── 2. Assign / remove editor
│   └── 3. Edit table of contents
├── 2. Production
│   ├── 1. Manage book editions
│   ├── 2. Manage publication issues
│   ├── 3. Manage articles
│   ├── 4. Manage chapters
│   ├── 5. Find books and articles (by topic / date / author)
│   ├── 6. Manage staff payments  ← Transaction 2
│   └── 7. Compare two issues
├── 3. Distribution & Payments
│   ├── 1. Manage distributors
│   ├── 2. Manage orders  ← Transaction 1
│   └── 3. Record payments & allocations
└── 4. Reports
    ├── 1. Revenue & expense reports
    ├── 2. Distributor reports
    ├── 3. Weekly / monthly orders
    ├── 4. Distributor count
    └── 5. Staff payment reports
```

## Transactions

**Transaction 1** — Bill distributor (Distribution → Manage orders → Bill):  
Atomically updates `billed_amount` on the order and inserts a `DISTRIBUTOR_PAYMENT` record. Rolls back both if either step fails.

**Transaction 2** — Enter and claim staff payment (Production → Manage staff payments → Enter and claim):  
Atomically inserts the payment and sets `claimed_date`. Rolls back both if either step fails.

## Project Structure

```
src/
├── main/        MainApp.java          — entry point
├── db/          DBConnection.java     — JDBC connection factory
├── service/     *Service.java         — menu logic
├── dao/         *DAO.java             — SQL operations
└── model/       *.java                — data model classes
```

## Database Connection

Defaults to `akulka26` on `classdb2.csc.ncsu.edu`. Override with environment variables:

```bash
export JDBC_URL='jdbc:mariadb://host:3306/dbname'
export JDBC_USER='user'
export JDBC_PASSWORD='password'
java -cp "out:mariadb-java-client-3.5.7.jar" main.MainApp
```
