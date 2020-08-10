public class Main {
    public static void main(String[] args) {
        int[] number = {13, 15, 24, 99, 14, 11, 1, 2, 3, 13, 13};
        quickSort(number);
        for(int i : number){
            System.out.println(i);
        }
    }

    public static void quickSort(int[] arr) {
        int left = 0;
        int right = arr.length-1;
        sort1(arr, left, right);
    }

    //sort array from index left to index right
    public static void sort1(int[] arr, int left, int right){
        if(left >= right){
            return;
        }
        int key = arr[left];
        int i = left;
        int j = right;

        while (i < j) {

            while (arr[j] > key && i < j) {
                j--;
            }
            while (arr[i] <= key && i < j) {
                i++;
            }
            if (i < j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }
        if(key > arr[j]){
            arr[left]= arr[j];
            arr[j] = key;
        }

        if(j-1 > left){
            sort1(arr, left, j-1);
        }
        if(j+1 < right){
            sort1(arr, j+1, right);
        }

    }
}
