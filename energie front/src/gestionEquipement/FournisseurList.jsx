import React, { useEffect, useState } from 'react';
import { fetchFournisseur, deleteFournisseur, search } from './services/FournisseursService';

function FournisseurList() {
  const [fournisseurs, setFournisseurs] = useState([]);
  const [searchQuery, setSearchQuery] = useState(''); // State for search input

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
      setFournisseurs(fournisseurs.filter((fournisseur) => fournisseur.fournisseur.value !== id));
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
    <div className="container mt-4">
      <div className="d-flex justify-content-between mt-5 mb-5">
        <h1 className="ps-5">Fournisseurs List</h1>
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            placeholder="Search Fournisseurs by Statut"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // Update searchQuery state
          />
          <button className="btn btn-primary" onClick={handleSearch}>Search</button>
        </div>
      </div>
      <table className="table table-hover table-bordered">
        <thead className="table-success text-center">
          <tr>
            <th>Nom</th>
            <th>Contact</th>
            <th>Statut</th>
            <th>Disponibilite</th>
            <th>Type Equipement</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {fournisseurs.length !== 0 ? (
            fournisseurs.map((fournisseur) => (
              <tr key={fournisseur.id}>
                <td className="text-center align-middle">{fournisseur.nom.value}</td>
                <td className="text-center align-middle">{fournisseur.contact.value}</td>
                <td className="text-center align-middle">{fournisseur.statut.value}</td>
                <td className="text-center align-middle">{fournisseur.disponibilite.value}</td>
                <td className="text-center align-middle">{fournisseur.type_equipement.value}</td>
                <td className="text-center align-middle">
                  <button
                    className="btn btn-danger"
                    onClick={() => handleDelete(fournisseur.fournisseur.value)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="6" className="text-center">No Fournisseurs available</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default FournisseurList;
