import { Navbar } from "./navbar";

export function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen relative">
      <Navbar />
      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8 relative z-10">
        {children}
      </main>
    </div>
  );
}

