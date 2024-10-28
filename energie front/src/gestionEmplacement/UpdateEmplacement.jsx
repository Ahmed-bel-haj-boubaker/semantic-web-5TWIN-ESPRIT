import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

const UpdateEmplacement = () => {
  const { conditionsEnvironnementales } = useParams(); // Retrieve the parameter from the URL
  const [emplacement, setEmplacement] = useState({
    conditions_environnementales: "",
    adresse: "",
    coordonnees: "",
  });

  useEffect(() => {
    // Fetch the current emplacement data to populate the form
    const fetchEmplacement = async () => {
      try {
        const response = await fetch(
          `http://localhost:9090/emplacement/${conditionsEnvironnementales}`
        );
        if (!response.ok) {
          throw new Error("Failed to fetch emplacement data");
        }
        const data = await response.json();
        setEmplacement(data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchEmplacement();
  }, [conditionsEnvironnementales]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `http://localhost:9090/emplacement/update/${conditionsEnvironnementales}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(emplacement),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update emplacement");
      }

      // Handle successful update (e.g., redirect or show a success message)
    } catch (error) {
      console.error("Error updating emplacement:", error);
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <h1 className="text-2xl font-semibold mb-4">Update Emplacement</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Conditions Environnementales
          </label>
          <input
            type="text"
            value={emplacement.conditions_environnementales}
            onChange={(e) =>
              setEmplacement({
                ...emplacement,
                conditions_environnementales: e.target.value,
              })
            }
            placeholder="Conditions Environnementales"
            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Adresse
          </label>
          <input
            type="text"
            value={emplacement.adresse}
            onChange={(e) =>
              setEmplacement({ ...emplacement, adresse: e.target.value })
            }
            placeholder="Adresse"
            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Coordonnées
          </label>
          <input
            type="text"
            value={emplacement.coordonnees}
            onChange={(e) =>
              setEmplacement({ ...emplacement, coordonnees: e.target.value })
            }
            placeholder="Coordonnées"
            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <button
          type="submit"
          className="w-full bg-blue-600 text-white font-bold py-2 rounded hover:bg-blue-700 transition duration-200"
        >
          Update
        </button>
      </form>
    </div>
  );
};

export default UpdateEmplacement;
