import axios from 'axios';

const API_URL = "http://localhost:9090/hedi"; // Set your API URL here

const fetchEquipements  = async () => {
  const response = await axios.get(API_URL);
  return response.data;
}

export const deleteEquipement = async (id) => {
    try {
      console.log(id)
      await axios.delete(API_URL+"/equipement", {
        params: { URI:id }
      });
    } catch (error) {
      throw new Error('Error deleting equipment');
    }
  };
  

const addEquipment = async (equipementData) => { // Change here to 'addEquipment'
  const response = await axios.post(API_URL, equipementData);
  return response.data;
}

// Export with consistent naming
export { fetchEquipements, addEquipment  };
