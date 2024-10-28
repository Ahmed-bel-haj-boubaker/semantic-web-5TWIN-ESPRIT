import React, { useEffect, useState } from "react";
import {
  fetchEquipements,
  deleteEquipement,
} from "./services/EquipementService";

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
      setEquipements(
        equipements.filter((equipement) => equipement.equipement.value !== id)
      );
    } catch (error) {
      console.error("Error deleting equipement:", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">
          Equipement List
        </h1>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-green-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-center">Type</th>
              <th className="py-3 px-6 text-center">Capacite</th>
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {equipements.length !== 0 ? (
              equipements.map((equipement, index) => (
                <tr
                  key={index}
                  className="border-b border-gray-200 hover:bg-gray-100"
                >
                  <td className="py-3 px-6 text-center text-gray-700">
                    {equipement.type.value}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {equipement.capacite.value}
                  </td>
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition duration-300"
                      onClick={() => handleDelete(equipement.equipement.value)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3" className="py-4 text-center text-gray-600">
                  No equipment available
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default EquipementList;
