import { useEffect, useState } from "react";

const ListProject = () => {
  const [projects, setProjects] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [currentProject, setCurrentProject] = useState({
    nom: "",
    capacite: "",
    dateDebut: "",
    dateFin: "",
  });

  useEffect(() => {
    loadProjects();
  }, []);

  const formatDate = (date) => {
    const d = new Date(date);
    return d.toISOString().slice(0, 19); // "YYYY-MM-DDTHH:MM:SS"
  };

  const loadProjects = async () => {
    try {
      const response = await fetch("http://localhost:9090/api/projet/all");
      if (!response.ok) throw new Error("Erreur de réseau.");
      const data = await response.json();

      // Format dates in fetched projects
      const formattedData = data.map((project) => ({
        ...project,
        dateDebut: formatDate(project.dateDebut),
        dateFin: formatDate(project.dateFin),
      }));

      setProjects(formattedData);
    } catch (error) {
      console.error("Erreur lors de la récupération des projets :", error);
    }
  };

  const handleSearch = () => {
    const filteredProjects = projects.filter((project) =>
      project.nom.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setProjects(filteredProjects);
  };

  const handleDelete = async (nom) => {
    try {
      const response = await fetch(`http://localhost:9090/api/projet/delete?nom=${nom}`, {
        method: "DELETE",
      });
      if (!response.ok) throw new Error("Échec de la suppression.");

      // Remove deleted project from state
      setProjects(projects.filter((project) => project.nom !== nom));
    } catch (error) {
      console.error("Erreur lors de la suppression :", error);
    }
  };

  const handleAddOrUpdateProject = async () => {
    try {
        const method = isEditMode ? "PUT" : "POST";
        const url = isEditMode ? `http://localhost:9090/api/projet/update` : `http://localhost:9090/api/projet/add`;

        const response = await fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(currentProject),
        });
        if (!response.ok) throw new Error(isEditMode ? "Échec de la mise à jour." : "Échec de l'ajout.");

        // Refresh projects list
        await loadProjects(); // Ensure you wait for the projects to load

        // Close the modal and reset form fields
        setIsModalOpen(false);
        setCurrentProject({ nom: "", capacite: "", dateDebut: "", dateFin: "" });
        setIsEditMode(false); // Reset to add mode
    } catch (error) {
        console.error("Erreur lors de l'ajout ou de la mise à jour :", error);
    }
};


  const handleEdit = (project) => {
    setCurrentProject(project);
    setIsEditMode(true);
    setIsModalOpen(true);
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold text-gray-800">Liste des Projets</h1>
        <div className="flex items-center gap-2">
          <input
            type="text"
            className="px-4 py-2 border border-gray-300 rounded-md"
            placeholder="Rechercher..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button className="px-4 py-2 bg-blue-500 text-white rounded-md" onClick={handleSearch}>
            Rechercher
          </button>
          <button
            className="px-4 py-2 bg-green-500 text-white rounded-md"
            onClick={() => {
              setIsModalOpen(true);
              setIsEditMode(false);
              setCurrentProject({ nom: "", capacite: "", dateDebut: "", dateFin: "" });
            }}
          >
            Ajouter Projet
          </button>
        </div>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
          <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
            <h2 className="text-2xl font-semibold mb-4">{isEditMode ? "Modifier un Projet" : "Ajouter un Projet"}</h2>
            <div className="space-y-4">
              <input
                type="text"
                className="w-full px-4 py-2 border rounded-md"
                placeholder="Nom du projet"
                value={currentProject.nom}
                onChange={(e) => setCurrentProject({ ...currentProject, nom: e.target.value })}
              />
              <input
                type="number"
                className="w-full px-4 py-2 border rounded-md"
                placeholder="Capacité"
                value={currentProject.capacite}
                onChange={(e) => setCurrentProject({ ...currentProject, capacite: e.target.value })}
              />
              <input
                type="datetime-local"
                className="w-full px-4 py-2 border rounded-md"
                value={currentProject.dateDebut}
                onChange={(e) => setCurrentProject({ ...currentProject, dateDebut: e.target.value })}
              />
              <input
                type="datetime-local"
                className="w-full px-4 py-2 border rounded-md"
                value={currentProject.dateFin}
                onChange={(e) => setCurrentProject({ ...currentProject, dateFin: e.target.value })}
              />
            </div>
            <div className="flex justify-end gap-2 mt-6">
              <button
                className="px-4 py-2 bg-gray-400 text-white rounded-md"
                onClick={() => setIsModalOpen(false)}
              >
                Annuler
              </button>
              <button
                className="px-4 py-2 bg-green-500 text-white rounded-md"
                onClick={handleAddOrUpdateProject}
              >
                {isEditMode ? "Modifier" : "Ajouter"}
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="overflow-x-auto mt-6">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-green-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-center">Nom</th>
              <th className="py-3 px-6 text-center">Capacité</th>
              <th className="py-3 px-6 text-center">Date de début</th>
              <th className="py-3 px-6 text-center">Date de fin</th>
              <th className="py-3 px-6 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {projects.length !== 0 ? (
              projects.map((project, index) => (
                <tr key={index} className="border-b border-gray-200 hover:bg-gray-100">
                  <td className="py-3 px-6 text-center text-gray-700">{project.nom}</td>
                  <td className="py-3 px-6 text-center text-gray-700">{project.capacite}</td>
                  <td className="py-3 px-6 text-center text-gray-700">{formatDate(project.dateDebut)}</td>
                  <td className="py-3 px-6 text-center text-gray-700">{formatDate(project.dateFin)}</td>
                  <td className="py-3 px-6 text-center">
                    <button
                      className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 mr-2"
                      onClick={() => handleEdit(project)}
                    >
                      Modifier
                    </button>
                    <button
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
                      onClick={() => handleDelete(project.nom)}
                    >
                      Supprimer
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="py-3 px-6 text-center text-gray-700">Aucun projet trouvé</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ListProject;
