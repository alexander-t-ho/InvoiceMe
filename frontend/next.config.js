/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api/v1',
  },
  // Enable prefetching for better navigation performance
  experimental: {
    optimizePackageImports: ['lucide-react'],
  },
}

module.exports = nextConfig


