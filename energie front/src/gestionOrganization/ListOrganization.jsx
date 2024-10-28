import { useEffect, useState } from "react";

const ListOrganization = () => {
  const [organizations, setOrganizations] = useState([]);
  const [searchQuery, setSearchQuery] = useState(""); // State for search input

  useEffect(() => {
    loadOrganizations();
  }, []);

  const loadOrganizations = async () => {
    try {
      const response = await fetch(
        "http://localhost:9090/organization/organizations"
      );
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setOrganizations(data); // Assuming the response is in the correct format
    } catch (error) {
      console.error("Error fetching organizations:", error);
    }
  };

  const handleSearch = () => {
    // Filtering organizations based on the search query
    const filteredOrganizations = organizations.filter((org) =>
      org.nom.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setOrganizations(filteredOrganizations);
  };

  const handleDelete = async (nom) => {
    try {
      const response = await fetch(
        `http://localhost:9090/organization/delete/${nom}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete organization");
      }

      const result = await response.json();
      console.log(result.message); // Log the success message

      // Update state to remove the deleted organization
      setOrganizations(organizations.filter((org) => org.nom !== nom));
    } catch (error) {
      console.error("Error deleting organization:", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">
          Organizations List
        </h1>
        <div className="flex items-center gap-2">
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Search Organizations by Name"
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
              <th className="py-3 px-6 text-center">Nom</th>
              {/* You can add more columns here if your Organization has additional fields */}
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {organizations.length !== 0 ? (
              organizations.map((org, index) => (
                <tr
                  key={index}
                  className="border-b border-gray-200 hover:bg-gray-100"
                >
                  <td className="py-3 px-6 text-center text-gray-700">
                    {org.nom}
                  </td>
                  {/* You can add more cells here for additional fields */}
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition duration-300"
                      onClick={() => handleDelete(org.nom)} // Call delete function with org.nom
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="2" className="py-4 text-center text-gray-600">
                  No Organizations available
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ListOrganization;
