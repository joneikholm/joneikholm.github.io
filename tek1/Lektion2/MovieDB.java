import java.util.ArrayList;
import java.util.List;

public class MovieDB {

    public static void main(String[] args) {
        Movie movie1 = new Movie(1, "Matrix");
        Movie movie2 = new Movie(2, "Verden udenfor");
        Movie movie3 = new Movie(3, "Das Boot");

        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);
        for(int i=0; i<movies.size(); i++){
            System.out.print(movies.get(i).id + " ");
            System.out.println(movies.get(i).name);
        }


    }
}
