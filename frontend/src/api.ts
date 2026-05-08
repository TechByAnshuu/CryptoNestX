import axios from 'axios';

// The Nginx reverse proxy handles /api routing to the backend microservices
const API_BASE = '/api';

export const portfolioApi = {
    getSummary: async (userId: string) => {
        const response = await axios.get(`${API_BASE}/portfolio/summary/${userId}`);
        return response.data;
    },
    // Get live prices for an array of symbols
    getPrices: async (symbols: string[]) => {
        const query = symbols.join(',');
        const response = await axios.get(`${API_BASE}/portfolio/prices?symbols=${query}`);
        return response.data;
    }
};
