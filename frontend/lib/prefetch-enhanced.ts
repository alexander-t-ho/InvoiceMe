import { QueryClient } from "@tanstack/react-query";
import { customerService } from "@/lib/services/CustomerService";
import { invoiceService } from "@/lib/services/InvoiceService";
import { itemsApi } from "@/lib/api/items";

const staleTime = 5 * 60 * 1000; // 5 minutes

/**
 * Enhanced prefetching with intelligent route detection and adjacent page prefetching.
 */
export function prefetchRouteData(queryClient: QueryClient, route: string) {
  switch (route) {
    case "/":
      prefetchDashboardData(queryClient);
      // Prefetch adjacent pages
      prefetchCustomersList(queryClient);
      prefetchInvoicesList(queryClient);
      break;
    case "/customers":
      prefetchCustomersList(queryClient);
      // Prefetch adjacent pages
      prefetchDashboardData(queryClient);
      prefetchInvoicesList(queryClient);
      break;
    case "/invoices":
      prefetchInvoicesList(queryClient);
      // Prefetch adjacent pages
      prefetchDashboardData(queryClient);
      prefetchCustomersList(queryClient);
      prefetchItemsList(queryClient);
      break;
    case "/items":
      prefetchItemsList(queryClient);
      // Prefetch adjacent pages
      prefetchDashboardData(queryClient);
      prefetchInvoicesList(queryClient);
      break;
    case "/payments":
      prefetchPaymentsData(queryClient);
      // Prefetch adjacent pages
      prefetchDashboardData(queryClient);
      prefetchInvoicesList(queryClient);
      break;
    case "/portal":
      prefetchPortalDashboard(queryClient);
      prefetchPortalInvoices(queryClient);
      break;
    case "/portal/invoices":
      prefetchPortalInvoices(queryClient);
      prefetchPortalDashboard(queryClient);
      break;
    default:
      // Handle dynamic routes
      if (route.startsWith("/invoices/") && route !== "/invoices") {
        const invoiceId = route.split("/invoices/")[1];
        if (invoiceId) {
          queryClient.prefetchQuery({
            queryKey: ["invoices", invoiceId],
            queryFn: () => invoiceService.getInvoiceById(invoiceId),
            staleTime,
          });
        }
      } else if (route.startsWith("/customers/") && route !== "/customers") {
        const customerId = route.split("/customers/")[1];
        if (customerId) {
          queryClient.prefetchQuery({
            queryKey: ["customers", customerId],
            queryFn: () => customerService.getCustomerById(customerId),
            staleTime,
          });
          // Also prefetch customer's invoices
          queryClient.prefetchQuery({
            queryKey: ["invoices", { customerId, page: 0, size: 20 }],
            queryFn: () => invoiceService.getAllInvoices({ customerId, page: 0, size: 20 }),
            staleTime,
          });
        }
      } else if (route.startsWith("/portal/invoices/") && route !== "/portal/invoices") {
        const invoiceId = route.split("/portal/invoices/")[1];
        if (invoiceId) {
          queryClient.prefetchQuery({
            queryKey: ["invoices", invoiceId],
            queryFn: () => invoiceService.getInvoiceById(invoiceId),
            staleTime,
          });
        }
      }
      break;
  }
}

/**
 * Prefetch dashboard data (minimal data for counts)
 */
function prefetchDashboardData(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["customers", 0, 1],
    queryFn: () => customerService.getAllCustomers(0, 1),
    staleTime,
  });
  queryClient.prefetchQuery({
    queryKey: ["invoices", { page: 0, size: 1 }],
    queryFn: () => invoiceService.getAllInvoices({ page: 0, size: 1 }),
    staleTime,
  });
  queryClient.prefetchQuery({
    queryKey: ["invoices", { status: "PAID", page: 0, size: 10 }],
    queryFn: () => invoiceService.getAllInvoices({ status: "PAID", page: 0, size: 10 }),
    staleTime,
  });
  queryClient.prefetchQuery({
    queryKey: ["items", 0, 1],
    queryFn: () => itemsApi.getAll(0, 1),
    staleTime,
  });
}

/**
 * Prefetch customers list
 */
function prefetchCustomersList(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["customers", 0, 20, "name"],
    queryFn: () => customerService.getAllCustomers(0, 20, "name"),
    staleTime,
  });
}

/**
 * Prefetch invoices list
 */
function prefetchInvoicesList(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["invoices", { page: 0, size: 20 }],
    queryFn: () => invoiceService.getAllInvoices({ page: 0, size: 20 }),
    staleTime,
  });
}

/**
 * Prefetch items list
 */
function prefetchItemsList(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["items", 0, 20],
    queryFn: () => itemsApi.getAll(0, 20),
    staleTime,
  });
}

/**
 * Prefetch payments data
 */
function prefetchPaymentsData(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["invoices", { status: "PAID", page: 0, size: 20 }],
    queryFn: () => invoiceService.getAllInvoices({ status: "PAID", page: 0, size: 20 }),
    staleTime,
  });
}

/**
 * Prefetch portal dashboard
 */
function prefetchPortalDashboard(queryClient: QueryClient) {
  // Portal dashboard data prefetching
  queryClient.prefetchQuery({
    queryKey: ["customer-invoices", "prefetch"],
    queryFn: async () => null,
    staleTime,
  });
}

/**
 * Prefetch portal invoices
 */
function prefetchPortalInvoices(queryClient: QueryClient) {
  queryClient.prefetchQuery({
    queryKey: ["customer-invoices", "prefetch"],
    queryFn: async () => null,
    staleTime,
  });
}

/**
 * Prefetch all main routes for instant navigation
 * Call this on app initialization or after login
 */
export function prefetchAllMainRoutes(queryClient: QueryClient) {
  // Prefetch all main navigation routes
  prefetchDashboardData(queryClient);
  prefetchCustomersList(queryClient);
  prefetchInvoicesList(queryClient);
  prefetchItemsList(queryClient);
  prefetchPaymentsData(queryClient);
}

