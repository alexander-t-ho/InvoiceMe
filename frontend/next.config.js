/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api/v1',
  },
  // Enable prefetching for better navigation performance
  experimental: {
    optimizePackageImports: ['lucide-react', '@radix-ui/react-dialog', '@radix-ui/react-select'],
  },
  // Enable automatic prefetching for all Link components
  // This is enabled by default in Next.js 14, but we're being explicit
  onDemandEntries: {
    // Period (in ms) where the server will keep pages in the buffer
    maxInactiveAge: 60 * 1000, // Increased to 60s for better prefetching
    // Number of pages that should be kept simultaneously without being disposed
    pagesBufferLength: 5, // Increased to keep more pages in memory
  },
  // Performance optimizations
  swcMinify: true,
  compiler: {
    removeConsole: process.env.NODE_ENV === 'production',
  },
  // Optimize images if you add them later
  images: {
    formats: ['image/avif', 'image/webp'],
  },
  // Enable compression
  compress: true,
}

module.exports = nextConfig


