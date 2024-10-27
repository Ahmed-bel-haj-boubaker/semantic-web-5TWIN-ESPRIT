import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import './App.css'
import AddEquipment from './gestionEquipement/AddEquipement'
import EquipementList from "./gestionEquipement/EquipementList";
import 'bootstrap/dist/css/bootstrap.min.css';
import AddFournisseur from "./gestionEquipement/AddFournisseur";
import FournisseurList from "./gestionEquipement/FournisseurList";
import Navbar from "./Navbar";

function App() {
  

  return (
    
    <Router>
       <Navbar />
      <Routes>
      <Route path="equipements" >
            <Route index element={<EquipementList />} /> 
            <Route path="add" element={<AddEquipment />} />
          </Route>
          <Route path="fournisseurs" >
            <Route index element={<FournisseurList />} /> 
            <Route path="add" element={<AddFournisseur />} />
          </Route>
        

        
      </Routes>
    </Router>
  )
}

export default App
