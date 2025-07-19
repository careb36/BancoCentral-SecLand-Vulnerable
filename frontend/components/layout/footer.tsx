export function Footer() {
  return (
    <footer className="bg-gray-800/90 text-white py-8 mt-12">
      <div className="container mx-auto px-4 text-center">
        <p className="mb-2">&copy; 2025 CentralBank SecLand - Deliberately Vulnerable Banking Application</p>
        <p className="text-sm opacity-80 mb-2">For Educational and Research Purposes Only</p>
        <p className="text-sm">
          <a
            href="https://github.com/careb36/BancoCentral-SecLand-Vulnerable"
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-400 hover:text-blue-300 underline"
          >
            GitHub Repository
          </a>
        </p>
        <p className="text-xs opacity-70 mt-2">
          This application is part of a Master's Thesis and is intentionally vulnerable.
        </p>
      </div>
    </footer>
  )
}
