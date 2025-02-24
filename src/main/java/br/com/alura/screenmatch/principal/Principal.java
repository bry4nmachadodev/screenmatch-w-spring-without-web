package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

        private Scanner leitura = new Scanner(System.in);

        private ConsumoApi consumo = new ConsumoApi();
        private ConverteDados conversor = new ConverteDados();

        private final String ENDERECO = "https://omdbapi.com/?t=";
        private final String API_KEY = "&apikey=6585022c";

        public void exibeMenu(){
            System.out.println("Digite o nome da série para busca:");
            var nomeSerie = leitura.nextLine();
            var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
            DadosSerie dados =  conversor.obterDados(json, DadosSerie.class);
            System.out.println(dados);

            List<DadosTemporada> temporadas = new ArrayList<>();

            for(int i = 1; i<=dados.totalTemporadas(); i++) {
                json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + "&season="+ i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            for (int i = 0; i < dados.totalTemporadas(); i++) {
                List<DadosEpisodios> episodiosTemporadas = temporadas.get(i).episodios();
                for (int j = 0; j < episodiosTemporadas.size(); j++){
                    System.out.println(episodiosTemporadas.get(j).titulo());
                }
            }

            temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//            List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico","Nico");
//
//            nomes.stream()
//                    .sorted()
//                    .limit(3)
//                    .filter(n -> n.startsWith("N"))
//                    .map(n -> n.toUpperCase())
//                    .forEach(System.out::println);

            List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream())
                    .collect(Collectors.toList());

//            System.out.println("\n Top 10 episódios");
//            dadosEpisodios.stream()
//                    .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                    .peek(e -> System.out.println("Primeiro filtro(n/a) " + e))
//                    .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
//                    .peek(e -> System.out.println("Ordenação " + e))
//                    .limit(10)
//                    .peek(e -> System.out.println("Limite " + e))
//                    .map(e -> e.titulo().toUpperCase())
//                    .peek(e -> System.out.println("Mapeamento " + e))
//                    .forEach(System.out::println);
//
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d)))
                    .collect(Collectors.toList());

            episodios.forEach(System.out::println);

//            System.out.println("Digite um trecho do titulo que deseja buscar!");
//            var trechoTitulo = leitura.nextLine();
//            Optional<Episodio> episodioBuscado = episodios.stream()
//                    .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                    .findFirst();
//            if(episodioBuscado.isPresent()){
//                System.out.println("Episódio encontrado!");
//                System.out.println("Temporada :" + episodioBuscado.get().getTemporada());
//            } else {
//                System.out.println("Episódio não encontrado.");
//            }
//
//            System.out.println("Apartir de que ano você deseja ver os episódios?");
//            var ano = leitura.nextInt();
//            leitura.nextLine();
//
//            LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            episodio.stream()
//                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                    .forEach(e -> System.out.println("Temporada: " + e.getTemporada() +
//                            " Episodio : " + e.getTitulo() +
//                            " Data de lançamento : " + e.getDataLancamento().format(formatador)));

            Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                    .filter(e -> e.getAvaliacao() > 0.0)
                    .collect(Collectors.groupingBy(Episodio::getTemporada,
                            Collectors.averagingDouble(Episodio::getAvaliacao)));

            System.out.println(avaliacoesPorTemporada);
        }

    }
