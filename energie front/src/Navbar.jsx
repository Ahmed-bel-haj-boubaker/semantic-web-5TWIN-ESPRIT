import { useState } from "react";
import { Link } from "react-router-dom";

function Navbar() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isEquipementsDropdownOpen, setIsEquipementsDropdownOpen] =
    useState(false);
  const [isorgDropdownOpen, setisorgDropdownOpen] = useState(false);
  const [isFournisseursDropdownOpen, setIsFournisseursDropdownOpen] =
    useState(false);
  const [isEmplacementDropdownOpen, setIsEmplacementDropdownOpen] =
    useState(false); // State for Emplacement dropdown

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  return (
    <nav className="bg-gradient-to-r from-indigo-500 to-blue-600 shadow-lg">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link
          className="text-3xl font-extrabold text-white tracking-wide"
          to="/"
        >
          Energy
        </Link>
        <button
          className="lg:hidden text-white focus:outline-none"
          onClick={toggleMobileMenu}
          aria-controls="navbarNav"
          aria-expanded={isMobileMenuOpen}
          aria-label="Toggle navigation"
        >
          <svg
            className="w-8 h-8"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M4 6h16M4 12h16M4 18h16"
            ></path>
          </svg>
        </button>

        <div className="hidden lg:flex space-x-8">
          <div
            onMouseEnter={() => setIsEmplacementDropdownOpen(true)}
            onClick={() => setIsEmplacementDropdownOpen(false)}
            className="relative"
          >
            <Link
              className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
              to="/emplacement"
            >
              Emplacement
            </Link>
            {isEmplacementDropdownOpen && (
              <div className="absolute left-0 mt-2 w-48 bg-white shadow-lg rounded-lg z-10">
                <Link
                  className="block px-4 py-2 text-gray-700 hover:bg-indigo-100 transition-colors duration-200"
                  to="/emplacement/add"
                >
                  Add Emplacement
                </Link>
              </div>
            )}
          </div>
          {/* Equipements with Dropdown */}
          <div
            onMouseEnter={() => setIsEquipementsDropdownOpen(true)}
            onClick={() => setIsFournisseursDropdownOpen(false)}
            className="relative"
          >
            <Link
              className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
              to="/equipements"
            >
              Equipements
            </Link>

            {isEquipementsDropdownOpen && (
              <div className="absolute left-0 mt-2 w-48 bg-white shadow-lg rounded-lg z-10">
                <Link
                  className="block px-4 py-2 text-gray-700 hover:bg-indigo-100 transition-colors duration-200"
                  to="/equipements/add"
                >
                  Add Equipment
                </Link>
              </div>
            )}
          </div>

          <div
            onMouseEnter={() => setIsFournisseursDropdownOpen(true)}
            onClick={() => setIsFournisseursDropdownOpen(false)}
            className="relative"
          >
            <Link
              className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
              to="/fournisseurs"
            >
              Fournisseurs
            </Link>

            {isFournisseursDropdownOpen && (
              <div className="absolute left-0 mt-2 w-48 bg-white shadow-lg rounded-lg z-10">
                <Link
                  className="block px-4 py-2 text-gray-700 hover:bg-indigo-100 transition-colors duration-200"
                  to="/fournisseurs/add"
                >
                  Add Fournisseur
                </Link>
              </div>
            )}
          </div>

          <div
            onMouseEnter={() => setisorgDropdownOpen(true)}
            onClick={() => setisorgDropdownOpen(false)}
            className="relative"
          >
            <Link
              className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
              to="/organization"
            >
              Organization
            </Link>

            {isorgDropdownOpen && (
              <div className="absolute left-0 mt-2 w-48 bg-white shadow-lg rounded-lg z-10">
                <Link
                  className="block px-4 py-2 text-gray-700 hover:bg-indigo-100 transition-colors duration-200"
                  to="/organization/add"
                >
                  Add Organization
                </Link>
              </div>
            )}
          </div>

          {/* New List Projects Link */}
          <Link
            className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
            to="/projects"
          >
            List Projects
          </Link>
          <Link
            className="text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg"
            to="/financements"
          >
            List Financements
          </Link>
        </div>
      </div>

      {/* Mobile Menu */}
      <div
        className={`lg:hidden transform transition-transform duration-300 ease-in-out ${
          isMobileMenuOpen
            ? "max-h-screen opacity-100"
            : "max-h-0 opacity-0 overflow-hidden"
        }`}
      >
        <div className="px-4 pt-2 pb-4 space-y-2 bg-indigo-600">
          {/* Equipements and Submenu */}
          <div>
            <Link
              className="block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
              to="/equipements"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              Equipements
            </Link>
            <Link
              className="ml-4 block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
              to="/equipements/add"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              Add Equipment
            </Link>
          </div>

          {/* Fournisseurs Links */}
          <Link
            className="block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
            to="/fournisseurs"
            onClick={() => setIsMobileMenuOpen(false)}
          >
            Fournisseurs
          </Link>
          <Link
            className="ml-4 block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
            to="/fournisseurs/add"
            onClick={() => setIsMobileMenuOpen(false)}
          >
            Add Fournisseur
          </Link>

          {/* New List Projects Link in Mobile Menu */}
          <Link
            className="block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
            to="/projects"
            onClick={() => setIsMobileMenuOpen(false)}
          >
            List Projects
          </Link>

          <Link
            className="block text-white hover:text-yellow-300 transition-colors duration-300 font-medium text-lg py-2"
            to="/financements"
            onClick={() => setIsMobileMenuOpen(false)}
          >
            List Financements
          </Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
