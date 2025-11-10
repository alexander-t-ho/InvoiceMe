#!/bin/bash

# Setup script to use Java 17 for InvoiceMe project
# Run this before building/running tests: source setup-java17.sh

export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ Java 17 configured"
echo "JAVA_HOME: $JAVA_HOME"
java -version


