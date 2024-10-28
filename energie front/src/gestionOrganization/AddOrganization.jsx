import React, { useState } from "react";

const AddOrganization = () => {
  const [nom, setNom] = useState(""); // State for the organization name
  const [message, setMessage] = useState(""); // State for success/error messages

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent page refresh

    try {
      const response = await fetch("http://localhost:9090/organization/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ nom }), // Sending the organization name as JSON
      });

      if (!response.ok) {
        throw new Error("Failed to add organization");
      }

      const result = await response.json();
      setMessage(result.message); // Display success message
      setNom(""); // Reset the input field after successful submission
    } catch (error) {
      console.error("Error adding organization:", error);
      setMessage("Error adding organization: " + error.message); // Display error message
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <h1 className="text-3xl font-semibold text-gray-800 mb-4">
        Add Organization
      </h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2" htmlFor="nom">
            Organization Name
          </label>
          <input
            type="text"
            id="nom"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={nom}
            onChange={(e) => setNom(e.target.value)} // Update state on input change
            required
          />
        </div>
        <button
          type="submit"
          className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
        >
          Add Organization
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

export default AddOrganization;
