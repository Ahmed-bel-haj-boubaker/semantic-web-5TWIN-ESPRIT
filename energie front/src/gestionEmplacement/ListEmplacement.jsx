import { useEffect, useState } from "react";
import { Link } from "react-router-dom"; // Import Link for navigation
import { useNavigate } from "react-router-dom";

const ListEmplacement = () => {
  const [emplacements, setEmplacements] = useState([]);
  const [searchQuery, setSearchQuery] = useState(""); // State for search input

  const navigate = useNavigate();
  useEffect(() => {
    loadEmplacements();
  }, []);

  const loadEmplacements = async () => {
    try {
      const response = await fetch(
        "http://localhost:9090/emplacement/list" // Adjust the endpoint as needed
      );
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setEmplacements(data); // Assuming the response is in the correct format
    } catch (error) {
      console.error("Error fetching emplacements:", error);
    }
  };

  const handleSearch = () => {
    // Filtering emplacements based on the search query
    const filteredEmplacements = emplacements.filter((emp) =>
      emp.conditions_environnementales
        .toLowerCase()
        .includes(searchQuery.toLowerCase())
    );
    setEmplacements(filteredEmplacements);
  };

  const handleDelete = async (conditionsEnvironnementales) => {
    try {
      const response = await fetch(
        `http://localhost:9090/emplacement/delete/${conditionsEnvironnementales}`, // Adjust the endpoint as needed
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete emplacement");
      }

      const result = await response.json();
      console.log(result.message); // Log the success message

      // Update state to remove the deleted emplacement
      setEmplacements(
        emplacements.filter(
          (emp) =>
            emp.conditions_environnementales !== conditionsEnvironnementales
        )
      );
    } catch (error) {
      console.error("Error deleting emplacement:", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">
          Emplacements List
        </h1>
        <div className="flex items-center gap-2">
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Search Emplacements by Conditions"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // Update searchQuery state
          />
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition duration-300"
            onClick={handleSearch}
          >
            Search
          </button>
        </div>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-green-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-center">
                Conditions Environnementales
              </th>
              <th className="py-3 px-6 text-center">Adresse</th>
              <th className="py-3 px-6 text-center">Coordonnées</th>
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {emplacements.length !== 0 ? (
              emplacements.map((emp, index) => (
                <tr
                  key={index}
                  className="border-b border-gray-200 hover:bg-gray-100"
                >
                  <td className="py-3 px-6 text-center text-gray-700">
                    {emp.conditions_environnementales}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {emp.adresse}
                  </td>
                  <td className="py-3 px-6 text-center text-gray-700">
                    {emp.coordonnees}
                  </td>
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition duration-300"
                      onClick={() =>
                        navigate(
                          `/emplacement/update/${emp.conditions_environnementales}`
                        )
                      } // Redirect to the update page
                    >
                      Update
                    </button>
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition duration-300"
                      onClick={() =>
                        handleDelete(emp.conditions_environnementales)
                      }
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4" className="py-4 text-center text-gray-600">
                  No Emplacements available
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ListEmplacement;
