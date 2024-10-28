import { useEffect, useState } from "react";

const ListeFinancement = () => {
  const [financements, setFinancements] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [newFinancement, setNewFinancement] = useState({ montantTotal: 0, sourceFinancement: "" });
  const [editingFinancement, setEditingFinancement] = useState(null);

  useEffect(() => {
    loadFinancements();
  }, []);

  const loadFinancements = async () => {
    try {
      const response = await fetch("http://localhost:9090/api/financement/all");
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setFinancements(data);
    } catch (error) {
      console.error("Erreur lors de la récupération des financements :", error);
    }
  };

  const handleSearch = () => {
    // Filtrer les financements en fonction de la requête de recherche
    const filteredFinancements = financements.filter((financement) =>
      financement.sourceFinancement.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFinancements(filteredFinancements);
  };

  const handleAddFinancement = async () => {
    try {
      const response = await fetch("http://localhost:9090/api/financement/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newFinancement),
      });

      if (!response.ok) {
        throw new Error("Échec de l'ajout du financement");
      }

      await response.text();
      loadFinancements(); // Reload the financements after adding
      setNewFinancement({ montantTotal: 0, sourceFinancement: "" }); // Reset form
    } catch (error) {
      console.error("Erreur lors de l'ajout du financement :", error);
    }
  };

  const handleUpdateFinancement = async () => {
    try {
      const response = await fetch("http://localhost:9090/api/financement/update", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(editingFinancement),
      });

      if (!response.ok) {
        throw new Error("Échec de la mise à jour du financement");
      }

      await response.text();
      loadFinancements(); // Reload the financements after updating
      setEditingFinancement(null); // Reset edit state
    } catch (error) {
      console.error("Erreur lors de la mise à jour du financement :", error);
    }
  };

  const handleDelete = async (sourceFinancement) => {
    try {
      const response = await fetch(`http://localhost:9090/api/financement/delete?sourceFinancement=${sourceFinancement}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new Error("Échec de la suppression du financement");
      }

      await response.text();
      loadFinancements(); // Reload the financements after deleting
    } catch (error) {
      console.error("Erreur lors de la suppression du financement :", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">Liste des Financements</h1>
        <div className="flex items-center gap-2">
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Rechercher des financements par source"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // Mettre à jour l'état searchQuery
          />
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
            onClick={handleSearch}
          >
            Rechercher
          </button>
        </div>
      </div>

      <div className="mb-6">
        <h2 className="text-xl font-semibold">Ajouter un Financement</h2>
        <div className="flex items-center gap-2">
          <input
            type="number"
            className="px-4 py-2 border border-gray-300 rounded-md"
            placeholder="Montant Total"
            value={newFinancement.montantTotal}
            onChange={(e) => setNewFinancement({ ...newFinancement, montantTotal: parseFloat(e.target.value) })} // Update amount
          />
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md"
            placeholder="Source de Financement"
            value={newFinancement.sourceFinancement}
            onChange={(e) => setNewFinancement({ ...newFinancement, sourceFinancement: e.target.value })} // Update source
          />
          <button
            className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition duration-300"
            onClick={handleAddFinancement}
          >
            Ajouter
          </button>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-green-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-center">Montant Total</th>
              <th className="py-3 px-6 text-center">Source de Financement</th>
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {financements.length !== 0 ? (
              financements.map((financement, index) => (
                <tr key={index} className="border-b border-gray-200 hover:bg-gray-100">
                  <td className="py-3 px-6 text-center text-gray-700">{financement.montantTotal}</td>
                  <td className="py-3 px-6 text-center text-gray-700">{financement.sourceFinancement}</td>
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-yellow-500 text-white px-4 py-2 rounded-lg hover:bg-yellow-600"
                      onClick={() => setEditingFinancement(financement)} // Set the financing to edit
                    >
                      Modifier
                    </button>
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 ml-2"
                      onClick={() => handleDelete(financement.sourceFinancement)}
                    >
                      Supprimer
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3" className="py-3 px-6 text-center text-gray-700">Aucun financement trouvé</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {editingFinancement && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold">Modifier le Financement</h2>
          <div className="flex items-center gap-2">
            <input
              type="number"
              className="px-4 py-2 border border-gray-300 rounded-md"
              placeholder="Montant Total"
              value={editingFinancement.montantTotal}
              onChange={(e) => setEditingFinancement({ ...editingFinancement, montantTotal: parseFloat(e.target.value) })} // Update amount
            />
            <input
              type="text"
              className="px-4 py-2 border border-gray-300 rounded-md"
              placeholder="Source de Financement"
              value={editingFinancement.sourceFinancement}
              onChange={(e) => setEditingFinancement({ ...editingFinancement, sourceFinancement: e.target.value })} // Update source
            />
            <button
              className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
              onClick={handleUpdateFinancement}
            >
              Mettre à jour
            </button>
            <button
              className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition duration-300"
              onClick={() => setEditingFinancement(null)} // Reset edit state
            >
              Annuler
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ListeFinancement;
