import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import { Providers } from './providers'
import { Toaster } from '@/components/ui/toaster'
import { Meteors } from '@/components/ui/meteors'
import { NavigationLoading } from '@/components/ui/navigation-loading'
import { ErrorSuppression } from '@/components/error-suppression'
import { ViewportPrefetchProvider } from '@/components/viewport-prefetch-provider'
import { AppPrefetch } from '@/components/app-prefetch'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'GimmeYoMoney - ERP Invoicing System',
  description: 'AI-Assisted Full-Stack ERP Invoicing System',
  icons: {
    icon: '/favicon.svg',
  },
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className="dark">
      <body className={`${inter.className} bg-[#0f1e35] text-slate-100 relative min-h-screen overflow-x-hidden`}>
        {/* Global Meteor Effect Background - Reduced for performance */}
        <div className="fixed inset-0 overflow-hidden pointer-events-none -z-10 print:hidden">
          <Meteors number={10} />
        </div>
        <div className="relative z-10">
          <Providers>
            <ViewportPrefetchProvider>
              <AppPrefetch />
              <ErrorSuppression />
              <NavigationLoading />
              {children}
              <Toaster />
            </ViewportPrefetchProvider>
          </Providers>
        </div>
      </body>
    </html>
  )
}


