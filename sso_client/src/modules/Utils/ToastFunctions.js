import Toastify from 'toastify-js';
import "toastify-js/src/toastify.css";


export function showSuccessToast(msg) {
    Toastify({
        text: 'SUCCESS: ' + msg,
        duration: 3000,
        gravity: 'top',
        position: 'center',
        stopOnFocus: true,
        style: {
            'padding': '5px 8px',
            'border-radius': '3px',
            'box-shadow': '0px 1px 5px 0px rgba(0,0,0,0.2)',
            'background': '#DFF6BD',
            'color': '#548E12'
        }
    }).showToast();
}

export function showErrorToast(msg) {
    Toastify({
        text: 'ERROR: ' + msg,
        gravity: 'top',
        position: 'center',
        stopOnFocus: true,
        style: {
            'padding': '5px 8px',
            'border-radius': '3px',
            'box-shadow': '0px 1px 5px 0px rgba(0,0,0,0.2)',
            'background': '#FFE8E8',
            'color': '#E66666'
        }
    }).showToast();
}