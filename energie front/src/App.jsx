import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import "./App.css";
import AddEquipment from "./gestionEquipement/AddEquipement";
import EquipementList from "./gestionEquipement/EquipementList";
import "bootstrap/dist/css/bootstrap.min.css";
import AddFournisseur from "./gestionEquipement/AddFournisseur";
import FournisseurList from "./gestionEquipement/FournisseurList";
import Navbar from "./Navbar";
import ListOrganization from "./gestionOrganization/ListOrganization";
import AddOrganization from "./gestionOrganization/AddOrganization";
import ListEmplacement from "./gestionEmplacement/ListEmplacement";
import AddEmplacement from "./gestionEmplacement/AddEmplacement";
import UpdateEmplacement from "./gestionEmplacement/UpdateEmplacement";

function App() {
  return (
    <Router>
      <Navbar />
      <div className="container mt-4">
        <Routes>
          <Route path="/equipements" element={<EquipementList />} />
          <Route path="/equipements/add" element={<AddEquipment />} />
          <Route path="/fournisseurs" element={<FournisseurList />} />
          <Route path="/fournisseurs/add" element={<AddFournisseur />} />
          <Route path="/organization" element={<ListOrganization />} />
          <Route path="/organization/add" element={<AddOrganization />} />
          <Route path="/emplacement" element={<ListEmplacement />} />
          <Route path="/emplacement/add" element={<AddEmplacement />} />
          <Route
            path="/emplacement/update/:conditionsEnvironnementales"
            element={<UpdateEmplacement />}
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
