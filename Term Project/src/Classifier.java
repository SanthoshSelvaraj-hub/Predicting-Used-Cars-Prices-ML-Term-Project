import java.io.*;
import java.util.*;

public class Classifier {
	
	private static String path="C:\\Users\\santh\\eclipse-workspace\\Term Project\\src\\CarData(Updated).csv";
	
	private static class Data {
		private double [] x = new double [8];
		private double y;

		public Data(double []x, double y) {
			for(int k=1; k<8; k++) {
				this.x[k] = x[k];
			}
			this.y = y;
		}
	}

	private static class Parameter {
		private double m[] = new double[8];

		public Parameter(double m[]) {
			for(int i=0; i<8; i++) {
				this.m[i] = m[i];
			}
		}
	}

	public static void main(String args[]) {
		System.out.println("Classification is in progress, please wait...");
		LinearModel model = new LinearModel(path);
		model.process();
		System.out.println();
		System.out.println("Parameters : "+ Arrays.toString(model.parameters()));
	    System.out.println("Accuaracy : "+ model.accuracy() +"%");

	}

	static class LinearModel {
		private String path;
		private Parameter parameter;
		private double accuracy=0;
		private static double split_ratio=0.75;

		private void gradientDescent(List<Data> points, Parameter parameter, double learningRate, int iterations) {
			for (int i = 0; i < iterations; i++) {
				double[] gd = stepGradient(points, parameter, learningRate);
				
				for(int l=0; l<8; l++) {
					parameter.m[l] -= learningRate * gd[l];
				}

			}

		}
		
		LinearModel(String path){
			this.path=path;
		}

		private double[] stepGradient(List<Data> points, Parameter parameter, double learningRate) {
			int N = points.size();
			double []params = {0,0,0,0,0,0,0,0};

			for (Data d : points) {
				
				params[0] += (2 / (float) N) * (-1 * (d.y - (getH(parameter,d))));
				
				for(int h=1; h<8; h++) {
					params[h] += (2 / (float) N) * (-1 * d.x[h] * (d.y - (getH(parameter,d))));
				}			

			}
			return params;
		}
		
		private double getH(Parameter parameter,Data d) {
			return parameter.m[7] * d.x[7] +parameter.m[6] * d.x[6] +parameter.m[5] * d.x[5] +parameter.m[4] * d.x[4] +
					parameter.m[3] * d.x[3] +parameter.m[2] * d.x[2] +parameter.m[1] * d.x[1] + parameter.m[0];
		}

		private List<Data> getDataPoints(String fileName) {
			List<Data> dataPoints = new ArrayList<>();
			try (FileReader fr = new FileReader(new File(fileName)); BufferedReader br = new BufferedReader(fr)) {
				String readline;
				while ((readline = br.readLine()) != null) {
					String[] str = readline.split(",");
		                
					double y = Double.parseDouble(str[2]);
					double []x = new double[8];
					for(int j=1; j<8; j++) {
						
						x[j] = Double.parseDouble(str[j]);
					}
										
					dataPoints.add(new Data(x, y));
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return dataPoints;
		}

		public void process() {

			List<Data> points = getDataPoints(path);
			
			double learningRate = .0000001;
			int iterations = 10000000;

			double[]m = {0,0,0,0,0,0,0,0};
			this.parameter = new Parameter(m);
			
			List[] list=split(points);

			List<Data> train_data=list[0];
			List<Data> test_data=list[1];

			gradientDescent(train_data, parameter, learningRate, iterations);
			accuracy(test_data,parameter);
			
		}
		
		

		public double accuracy() {
			return accuracy;
		}
		
		public double[] parameters() {
			double[]m = {0,0,0,0,0,0,0,0};
			
			for(int i=0; i<8; i++) {		
				m[i]= parameter.m[i];
			}
			
			return m;
			
		}

		
		public void accuracy(List<Data> points,Parameter parameter) {
			   double sum=0;

				for (Data d : points) {
					double prediction=getH(parameter,d);
		                    
		            sum+=Math.min(prediction,d.y)/Math.max(prediction,d.y);
				}
				
		        this.accuracy=(sum/points.size())*100;
		}
		
	    public static List[] split(List<Data> list) 
	    { 
	        List<Data> first = new ArrayList<>(); 
	        List<Data> second = new ArrayList<>(); 
	  
	        int size = list.size(); 
	  

	        for (int i = 0; i < (int)(size*split_ratio); i++) 
	            first.add(list.get(i)); 
	  

	        for (int i = (int) (size*split_ratio); i < size; i++) 
	            second.add(list.get(i)); 
	  
	        return new List[] { first, second }; 
	    } 

	}

}

