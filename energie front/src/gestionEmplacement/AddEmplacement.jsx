import React, { useState } from "react";

const AddLocation = () => {
  const [name, setName] = useState(""); // State for the location name
  const [address, setAddress] = useState(""); // State for the location address
  const [environmentalConditions, setEnvironmentalConditions] = useState(""); // State for environmental conditions
  const [coordinates, setCoordinates] = useState(""); // State for coordinates
  const [message, setMessage] = useState(""); // State for success/error messages

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent page refresh

    try {
      const response = await fetch("http://localhost:9090/emplacement/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          adresse: address, // Mapping to Emplacement class attribute
          conditions_environnementales: environmentalConditions, // Mapping to Emplacement class attribute
          coordonnees: coordinates, // Mapping to Emplacement class attribute
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to add location");
      }

      const result = await response.json();
      setMessage(result.message); // Display success message
      setName(""); // Reset the input field after successful submission
      setAddress(""); // Reset the address field
      setEnvironmentalConditions(""); // Reset environmental conditions field
      setCoordinates(""); // Reset coordinates field
    } catch (error) {
      console.error("Error adding location:", error);
      setMessage("Error adding location: " + error.message); // Display error message
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <h1 className="text-3xl font-semibold text-gray-800 mb-4">
        Add Location
      </h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2" htmlFor="address">
            Address
          </label>
          <input
            type="text"
            id="address"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={address}
            onChange={(e) => setAddress(e.target.value)} // Update state on input change
            required
          />
        </div>
        <div className="mb-4">
          <label
            className="block text-gray-700 mb-2"
            htmlFor="environmentalConditions"
          >
            Environmental Conditions
          </label>
          <input
            type="text"
            id="environmentalConditions"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={environmentalConditions}
            onChange={(e) => setEnvironmentalConditions(e.target.value)} // Update state on input change
            required
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2" htmlFor="coordinates">
            Coordinates
          </label>
          <input
            type="text"
            id="coordinates"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={coordinates}
            onChange={(e) => setCoordinates(e.target.value)} // Update state on input change
            required
          />
        </div>
        <button
          type="submit"
          className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
        >
          Add Location
        </button>
      </form>
      {message && (
        <div className="mt-4 text-center">
          <p className="text-green-500">{message}</p>{" "}
          {/* Display success/error message */}
        </div>
      )}
    </div>
  );
};

export default AddLocation;
