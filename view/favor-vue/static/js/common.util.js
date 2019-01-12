function pad(n, width) {
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
}

function findIndex(arr, str){
    for(let i=0; i<arr.length; i++){
        if(arr[i] == str){
            return i;
        }
    }
    return -1;
}

function substring(str, start, end){
    return str.substring(start, end);
}