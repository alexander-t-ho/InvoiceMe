# Running Domain Model Tests

## Quick Start

### Prerequisites
- Java 17+ installed
- Gradle wrapper (already set up)

### Run All Domain Tests
\`\`\`bash
cd backend
./gradlew test --tests "com.invoiceme.domain.*"
\`\`\`

### Current Status
✅ Domain Model: 13 files implemented
✅ Tests: 29 test cases written
⚠️ Java Version: Java 17+ required (currently Java 11 detected)

### Install Java 17
\`\`\`bash
# macOS with Homebrew
brew install openjdk@17

# Then set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
\`\`\`

See [docs/JAVA_SETUP.md](docs/JAVA_SETUP.md) for detailed instructions.

### Verify Setup
\`\`\`bash
cd backend
./verify-domain.sh
\`\`\`
