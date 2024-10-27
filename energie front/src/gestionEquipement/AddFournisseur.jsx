import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Make sure to import useNavigate
import { addFournisseur } from './services/FournisseursService'; 

function AddFournisseur() {
  const [fournisseur, setFournisseur] = useState({
    nom: "",
    contact: "",
    statut : "",
    type_equipement :"",
    disponibilite: ""
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFournisseur({
      ...fournisseur,
      [name]: value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    addFournisseur(fournisseur)
      .then(response => {
        navigate("/fournisseurs"); // Change the route as needed
      })
      .catch(error => {
        console.error("Error adding equipment:", error);
      });
  };

  return (
    <div className="container mt-4">
      <div className='d-flex justify-content-between mt-5 mb-5'>
        <h1 className='ps-5'>Add Fournisseur</h1>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="mb-3 row">
          <label htmlFor='nom' className="col-2 col-form-label">Nom</label>
          <div className="col-10">
            <input
              id='nom'
              type="text"
              name="nom"
              value={fournisseur.nom}
              onChange={handleChange}
              placeholder='Enter Fournisseur nom'
              required
              className="form-control"
            />
          </div>
        </div>

        <div className="mb-3 row">
          <label htmlFor='contact' className="col-2 col-form-label">contact</label>
          <div className="col-10">
            <input
              id='contact'
              type="text"
              name="contact"
              value={fournisseur.contact}
              onChange={handleChange}
              placeholder='Enter Fournisseur contact'
              required
              className="form-control"
            />
          </div>
        </div>

        <div className="mb-3 row">
          <label htmlFor='statut' className="col-2 col-form-label">statut</label>
          <div className="col-10">
            <input
              id='statut'
              type="text"
              name="statut"
              value={fournisseur.statut}
              onChange={handleChange}
              placeholder='Enter Fournisseur statut'
              required
              className="form-control"
            />
          </div>
        </div>

        <div className="mb-3 row">
          <label htmlFor='type_equipement' className="col-2 col-form-label">type_equipement</label>
          <div className="col-10">
            <input
              id='type_equipement'
              type="text"
              name="type_equipement"
              value={fournisseur.type_equipement}
              onChange={handleChange}
              placeholder='Enter Fournisseur type_equipement'
              required
              className="form-control"
            />
          </div>
         
        </div>

        <div className="mb-3 row">
          <label htmlFor='disponibilite' className="col-2 col-form-label">disponibilite</label>
          <div className="col-10">
            <input
              id='disponibilite'
              type="text"
              name="disponibilite"
              value={fournisseur.disponibilite}
              onChange={handleChange}
              placeholder='Enter Fournisseur disponibilite'
              required
              className="form-control"
            />
          </div>
         
        </div>
        
        <button type="submit" className="btn btn-success">Add Fournisseur</button>
      </form>
    </div>
  );
}

export default AddFournisseur;
