import axios from 'axios';

const API_URL = "http://localhost:9090/hedi"; // Set your API URL here

const fetchFournisseur  = async () => {
  const response = await axios.get(API_URL+"/fournisseurs");
  return response.data;
}
export const deleteFournisseur = async (id) => {
    try {
      await axios.delete(API_URL+"/fournisseur", {
        params: { URI:id }
      });
    } catch (error) {
      throw new Error('Error deleting fournisseur');
    }
  };

const addFournisseur = async (equipementData) => { // Change here to 'addEquipment'
  const response = await axios.post(API_URL+"/addFournisseur", equipementData);
  return response.data;
}


const search= async (search) => {
    const response = await axios.get(`${API_URL}/search${search ? '/' + search : ''}`);
    return response.data;
  };

// Export with consistent naming
export { fetchFournisseur, addFournisseur , search  };
