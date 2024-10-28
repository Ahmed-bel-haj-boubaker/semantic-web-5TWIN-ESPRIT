import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { addFournisseur } from "./services/FournisseursService";

function AddFournisseur() {
  const [fournisseur, setFournisseur] = useState({
    nom: "",
    contact: "",
    statut: "",
    type_equipement: "",
    disponibilite: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFournisseur({
      ...fournisseur,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    addFournisseur(fournisseur)
      .then((response) => {
        navigate("/fournisseurs");
      })
      .catch((error) => {
        console.error("Error adding equipment:", error);
      });
  };

  return (
    <div className="container mx-auto max-w-lg mt-10 p-6 bg-white shadow-lg rounded-lg">
      <h1 className="text-3xl font-semibold text-gray-800 text-center mb-6">
        Add Fournisseur
      </h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        {[
          {
            id: "nom",
            label: "Nom",
            placeholder: "Enter Fournisseur nom",
            value: fournisseur.nom,
          },
          {
            id: "contact",
            label: "Contact",
            placeholder: "Enter Fournisseur contact",
            value: fournisseur.contact,
          },
          {
            id: "statut",
            label: "Statut",
            placeholder: "Enter Fournisseur statut",
            value: fournisseur.statut,
          },
          {
            id: "type_equipement",
            label: "Type Equipement",
            placeholder: "Enter Fournisseur type_equipement",
            value: fournisseur.type_equipement,
          },
          {
            id: "disponibilite",
            label: "Disponibilite",
            placeholder: "Enter Fournisseur disponibilite",
            value: fournisseur.disponibilite,
          },
        ].map(({ id, label, placeholder, value }) => (
          <div key={id} className="flex flex-col">
            <label htmlFor={id} className="text-gray-700 mb-1 font-medium">
              {label}
            </label>
            <input
              id={id}
              type="text"
              name={id}
              value={value}
              onChange={handleChange}
              placeholder={placeholder}
              required
              className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500"
            />
          </div>
        ))}
        <button
          type="submit"
          className="w-full bg-indigo-600 text-white font-medium py-2 rounded-lg hover:bg-indigo-700 transition duration-300"
        >
          Add Fournisseur
        </button>
      </form>
    </div>
  );
}

export default AddFournisseur;
