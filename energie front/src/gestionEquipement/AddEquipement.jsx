import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Make sure to import useNavigate
import { addEquipment } from './services/EquipementService'; 

function AddEquipment() {
  const [equipment, setEquipment] = useState({
    type: "",
    capacite: ""
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEquipment({
      ...equipment,
      [name]: value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    addEquipment(equipment)
      .then(response => {
        navigate("/equipements"); // Change the route as needed
      })
      .catch(error => {
        console.error("Error adding equipment:", error);
      });
  };

  return (
    <div className="container mt-4">
      <div className='d-flex justify-content-between mt-5 mb-5'>
        <h1 className='ps-5'>Add Equipment</h1>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="mb-3 row">
          <label htmlFor='type' className="col-2 col-form-label">Type</label>
          <div className="col-10">
            <input
              id='type'
              type="text"
              name="type"
              value={equipment.type}
              onChange={handleChange}
              placeholder='Enter Equipment Type'
              required
              className="form-control"
            />
          </div>
        </div>
        <div className="mb-3 row">
          <label htmlFor='capacite' className="col-2 col-form-label">Capacite</label>
          <div className='col-10'>
            <input
              id='capacite'
              type="text"
              name="capacite"
              value={equipment.capacite}
              onChange={handleChange}
              placeholder='Enter Capacite'
              required
              className="form-control"
            />
          </div>
        </div>
        <button type="submit" className="btn btn-success">Add Equipment</button>
      </form>
    </div>
  );
}

export default AddEquipment;
