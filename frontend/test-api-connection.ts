/**
 * Simple test script to verify API client configuration.
 * Run with: npx tsx test-api-connection.ts
 * (or ts-node if available)
 */

import { apiClient } from "./lib/api/client";
import { customersApi } from "./lib/api/customers";

async function testApiConnection() {
  console.log("🧪 Testing API Connection...\n");
  
  try {
    console.log("1. Testing API client configuration...");
    console.log(`   Base URL: ${apiClient.defaults.baseURL}`);
    console.log(`   Timeout: ${apiClient.defaults.timeout}ms`);
    console.log("   ✅ API client configured\n");
    
    console.log("2. Testing customers API endpoint...");
    const result = await customersApi.getAll(0, 5);
    console.log(`   ✅ Success! Retrieved ${result.content.length} customers`);
    console.log(`   Total: ${result.totalElements}, Page: ${result.page}, Size: ${result.size}\n`);
    
    console.log("✅ All API connection tests passed!");
    return true;
  } catch (error: any) {
    console.error("❌ API connection test failed:");
    if (error.code === "ECONNREFUSED") {
      console.error("   Backend server is not running.");
      console.error("   Please start the backend with: cd backend && ./gradlew bootRun");
    } else {
      console.error(`   Error: ${error.message}`);
    }
    return false;
  }
}

// Run test if executed directly
if (require.main === module) {
  testApiConnection()
    .then((success) => process.exit(success ? 0 : 1))
    .catch((error) => {
      console.error("Unexpected error:", error);
      process.exit(1);
    });
}

export { testApiConnection };

