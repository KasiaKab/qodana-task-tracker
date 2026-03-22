# Task Tracker – Qodana CI Practice Project

## What is this project?

A minimal task tracker REST API built with **Spring Boot 3.3**, **Java 21**, **MongoDB**, and **Lombok**.  
The application exposes basic CRUD operations for tasks and is containerised with Docker / Docker Compose.

**Tech stack**

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 (Web, Data MongoDB, Validation, Actuator) |
| Database | MongoDB |
| Build tool | Maven |
| Container | Docker / Docker Compose |

---

## Why the code is intentionally bad

The source code was written with deliberate code-quality issues – unused variables, raw types, missing null checks, overly complex methods, and other common Java anti-patterns.  
The sole purpose is to give **Qodana** something meaningful to detect, so the full CI quality-gate and auto-fix flow can be demonstrated end-to-end.

---

## Qodana setup

Qodana is configured through two files:

### `qodana.yaml`

Defines the analysis profile and linter image used in the pipeline:

```yaml
version: "1.0"
profile:
  name: qodana.recommended   # JetBrains recommended inspection set
linter: jetbrains/qodana-jvm:2025.2
fixesStrategy: apply          # automatically apply available fixes
projectJDK: "23"
```

### `.github/workflows/qodana_code_quality.yml`

GitHub Actions workflow that runs on every push / pull request to `main`:

```yaml
- name: 'Qodana Scan'
  uses: JetBrains/qodana-action@v2025.2
  with:
    args: --apply-fixes      # tell Qodana to apply fixes
    pr-mode: false
    push-fixes: pull-request # push fixed code as a new PR
  env:
    QODANA_TOKEN: ${{ secrets.QODANA_TOKEN }}
```

The `QODANA_TOKEN` secret must be set in the repository settings (obtained from [qodana.cloud](https://qodana.cloud)).

---

## How auto-fix works

1. A push or pull-request to `main` triggers the **Qodana** GitHub Actions job.
2. Qodana scans the codebase using the `qodana.recommended` profile.
3. With `--apply-fixes` passed as an argument and `fixesStrategy: apply` set in `qodana.yaml`, Qodana applies all automatically fixable issues directly to the checked-out code.
4. Because `push-fixes: pull-request` is configured, the action commits the fixed files to a new branch and opens a Pull Request against `main` with the applied changes.
5. The original PR/push also receives a Qodana checks annotation listing every detected issue, including those that could not be fixed automatically.
