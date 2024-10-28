import { useEffect, useState } from "react";
import {
  fetchFournisseur,
  deleteFournisseur,
  search,
} from "./services/FournisseursService";

function FournisseurList() {
  const [fournisseurs, setFournisseurs] = useState([]);
  const [searchQuery, setSearchQuery] = useState(""); // State for search input

  useEffect(() => {
    loadFournisseurs();
  }, []);

  const loadFournisseurs = async () => {
    try {
      const data = await fetchFournisseur();
      setFournisseurs(data);
    } catch (error) {
      console.error("Error fetching fournisseurs:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteFournisseur(id);
      setFournisseurs(
        fournisseurs.filter(
          (fournisseur) => fournisseur.fournisseur.value !== id
        )
      );
    } catch (error) {
      console.error("Error deleting fournisseur:", error);
    }
  };

  const handleSearch = async () => {
    try {
      const results = await search(searchQuery); // Call the search function
      setFournisseurs(results); // Update fournisseurs with search results
    } catch (error) {
      console.error("Error searching fournisseurs:", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">
          Fournisseurs List
        </h1>
        <div className="flex items-center gap-2">
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Search Fournisseurs by Statut"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // Update searchQuery state
          />
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
            onClick={handleSearch}
          >
            Search
          </button>
        </div>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-green-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-center">Nom</th>
              <th className="py-3 px-6 text-center">Contact</th>
              <th className="py-3 px-6 text-center">Statut</th>
              <th className="py-3 px-6 text-center">Disponibilite</th>
              <th className="py-3 px-6 text-center">Type Equipement</th>
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {fournisseurs.length !== 0 ? (
              fournisseurs.map((fournisseur) => (
                <tr
                  key={fournisseur.id}
                  className="border-b border-gray-200 hover:bg-gray-100"
                >
                  <td className="py-3 px-6 text-center text-gray-700">
                    {fournisseur.nom.value}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {fournisseur.contact.value}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {fournisseur.statut.value}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {fournisseur.disponibilite.value}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {fournisseur.type_equipement.value}
                  </td>
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition duration-300"
                      onClick={() =>
                        handleDelete(fournisseur.fournisseur.value)
                      }
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" className="py-4 text-center text-gray-600">
                  No Fournisseurs available
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default FournisseurList;
