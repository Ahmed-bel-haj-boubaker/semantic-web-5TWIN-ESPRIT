import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { addEquipment } from "./services/EquipementService";

function AddEquipment() {
  const [equipment, setEquipment] = useState({
    type: "",
    capacite: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEquipment({
      ...equipment,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    addEquipment(equipment)
      .then((response) => {
        navigate("/equipements");
      })
      .catch((error) => {
        console.error("Error adding equipment:", error);
      });
  };

  return (
    <div className="container mx-auto max-w-lg mt-10 p-6 bg-white shadow-md rounded-lg">
      <h1 className="text-3xl font-semibold text-gray-800 text-center mb-6">
        Add Equipment
      </h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="flex flex-col">
          <label htmlFor="type" className="text-gray-700 mb-1 font-medium">
            Type
          </label>
          <input
            id="type"
            type="text"
            name="type"
            value={equipment.type}
            onChange={handleChange}
            placeholder="Enter Equipment Type"
            required
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500"
          />
        </div>
        <div className="flex flex-col">
          <label htmlFor="capacite" className="text-gray-700 mb-1 font-medium">
            Capacite
          </label>
          <input
            id="capacite"
            type="text"
            name="capacite"
            value={equipment.capacite}
            onChange={handleChange}
            placeholder="Enter Capacite"
            required
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500"
          />
        </div>
        <button
          type="submit"
          className="w-full bg-indigo-600 text-white font-medium py-2 rounded-lg hover:bg-indigo-700 transition duration-300"
        >
          Add Equipment
        </button>
      </form>
    </div>
  );
}

export default AddEquipment;
