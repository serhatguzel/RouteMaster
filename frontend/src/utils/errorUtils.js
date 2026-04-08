export const getErrorMessage = (error) => {
    if (error.response) {
        const data = error.response.data;

        if (data && data.detail) {
            return data.detail;
        }

        if (data && data.message) {
            return data.message;
        }

        if (error.response.status === 403) return 'You do not have permission for this action.';
        if (error.response.status === 401) return 'Session expired. Please login again.';
        if (error.response.status === 500) return 'Internal server error. Please try again later.';
    }

    if (error.request) {
        return 'Server is unreachable. Please check your connection.';
    }

    return error.message || 'An unexpected error occurred.';
};
