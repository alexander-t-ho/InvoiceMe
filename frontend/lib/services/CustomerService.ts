import { customersApi } from "@/lib/api/customers";
import type {
  CustomerResponse,
  CreateCustomerRequest,
  UpdateCustomerRequest,
  PagedResponse,
} from "@/types/api";

/**
 * Customer Service (ViewModel layer in MVVM pattern).
 * Provides business logic and data transformation for customer operations.
 */
export class CustomerService {
  /**
   * Get all customers with pagination.
   */
  async getAllCustomers(
    page: number = 0,
    size: number = 20,
    sortBy: string = "name"
  ): Promise<PagedResponse<CustomerResponse>> {
    return customersApi.getAll(page, size, sortBy);
  }

  /**
   * Get customer by ID.
   */
  async getCustomerById(id: string): Promise<CustomerResponse> {
    return customersApi.getById(id);
  }

  /**
   * Authenticate customer by email and password.
   */
  async authenticateCustomer(email: string, password: string): Promise<CustomerResponse> {
    return customersApi.authenticate(email, password);
  }

  /**
   * Get customer by email.
   */
  async getCustomerByEmail(email: string): Promise<CustomerResponse> {
    return customersApi.getByEmail(email);
  }

  /**
   * Create a new customer.
   */
  async createCustomer(
    data: CreateCustomerRequest
  ): Promise<CustomerResponse> {
    return customersApi.create(data);
  }

  /**
   * Update an existing customer.
   */
  async updateCustomer(
    id: string,
    data: UpdateCustomerRequest
  ): Promise<CustomerResponse> {
    return customersApi.update(id, data);
  }

  /**
   * Delete a customer.
   */
  async deleteCustomer(id: string): Promise<void> {
    return customersApi.delete(id);
  }
}

// Export singleton instance
export const customerService = new CustomerService();

