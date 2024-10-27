import React, { useEffect, useState } from 'react';
import { fetchEquipements, deleteEquipement } from './services/EquipementService';

function EquipementList() {
  const [equipements, setEquipements] = useState([]);

  useEffect(() => {
    loadEquipements();
  }, []);

  const loadEquipements = async () => {
    try {
      const data = await fetchEquipements();
      setEquipements(data);
    } catch (error) {
      console.error("Error fetching equipements:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteEquipement(id);
      setEquipements(equipements.filter(equipement => equipement.equipement.value !== id));
    } catch (error) {
      console.error("Error deleting equipement:", error);
    }
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between mt-5 mb-5">
        <h1 className="ps-5">Equipement List</h1>
      </div>
      <table className="table table-hover table-bordered">
        <thead className="table-success text-center">
          <tr>
            <th>Type</th>
            <th>Capacite</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {equipements.length !== 0 ? (
            equipements.map((equipement, index) => (
              <tr key={index}>
                <td className="text-center align-middle">{equipement.type.value}</td>
                <td className="text-center align-middle">{equipement.capacite.value}</td>
                <td className="text-center align-middle">
                  <button 
                    className="btn btn-danger"
                    onClick={() => handleDelete(equipement.equipement.value)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="3" className="text-center">No equipment available</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default EquipementList;
