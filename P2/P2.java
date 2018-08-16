import java.util.*;

class Solution {

	static TreeMap<Integer, TreeMap<Character, TreeSet<Integer>>> fa;
	static TreeSet<Integer> states;
	static TreeSet<Character> inputs;
	static TreeSet<Integer> finals;
	static TreeMap<Integer, TreeSet<Integer>> eClosure;

	static void init() {
		fa = new TreeMap<Integer, TreeMap<Character, TreeSet<Integer>>>();
		states = new TreeSet<Integer>();
		inputs = new TreeSet<Character>();
		finals = new TreeSet<Integer>();
		eClosure = new TreeMap<Integer, TreeSet<Integer>>();
	}

	static void read() {
		Scanner sc = new Scanner(System.in);
		System.out.print("No. of transitions: ");
		int t = sc.nextInt();
		System.out.println("Transition input format:\n'<start_state> <input> <end_state>'");
		System.out.println("Epsilon input is denoted by 'E'");
		while(t-- > 0) {
			int s = sc.nextInt();
			char i = sc.next().charAt(0);
			int e = sc.nextInt();
			states.add(s);
			states.add(e);
			inputs.add(i);
			TreeMap<Character, TreeSet<Integer>> hm = new TreeMap<Character, TreeSet<Integer>>();
			TreeSet<Integer> al = new TreeSet<Integer>();
			if(fa.containsKey(s)) {
				hm = fa.get(s);
				if(hm.containsKey(i)) al = hm.get(i);
			}
			al.add(e);
			hm.put(i, al);
			fa.put(s,hm);
		}
		System.out.print("Final states (separated by a single space): ");
		while(sc.hasNext()) finals.add(sc.nextInt());
		sc.close();
		inputs.remove(Character.valueOf('E'));
		System.out.println("\n-----End of input-----");
	}
	
	static void show() {
		for(Integer node : fa.keySet()) {
			TreeMap<Character, TreeSet<Integer>> temp = fa.get(node);
			for(Character input : temp.keySet()) {
				System.out.println(node + "\t" + input + "\t" + temp.get(input));
			}
		}
	}
	
	static TreeSet<Integer> BFS(Integer start) {
		TreeSet<Integer> path = new TreeSet<Integer>();
		TreeSet<Integer> visited = new TreeSet<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(start);
		while(!queue.isEmpty()) {
			int node = (int)queue.poll();
			visited.add(node);
			//System.out.print(node + " ");
			path.add(node);
			try {
				TreeSet<Integer> al = fa.get(node).get('E');
				for(Integer ch : al) {
					if(!visited.contains(ch)) {
						queue.add(ch);
					}
				}
			}
			catch(Exception e) {
				continue;			
			}
		}
		return path;
	}

	static void getEpsilonClosure() {
		for(Integer i : states) {
			eClosure.put(i, BFS(i));
		}
		//for(Integer i : eClosure.keySet()) System.out.println(i + "\t" + eClosure.get(i));
	}

	static void enfaToNfa() {
		TreeMap<Integer, TreeMap<Character, TreeSet<Integer>>> temp_fa = new TreeMap<Integer, TreeMap<Character, TreeSet<Integer>>>();
		for(Integer i : states) {
			TreeSet<Integer> temp = eClosure.get(i);
			for(Integer j : temp) {
				if(finals.contains(j)) finals.add(i);
				for(Character ch : inputs) {
					if(!fa.containsKey(j) || !fa.get(j).containsKey(ch)) continue; 
					TreeSet<Integer> temp1 = fa.get(j).get(ch);
					for(Integer k : temp1) {
						TreeSet<Integer> temp2 = eClosure.get(k);
						for(Integer l : temp2) {
							TreeMap<Character, TreeSet<Integer>> hm = new TreeMap<Character, TreeSet<Integer>>();
							TreeSet<Integer> al = new TreeSet<Integer>();
							if(temp_fa.containsKey(i)) {
								hm = temp_fa.get(i);
								if(hm.containsKey(ch)) al = hm.get(ch);
							}
							al.add(l);
							hm.put(ch, al);
							temp_fa.put(i,hm);
						}
					}
				}
			}
		}
		fa = temp_fa;
		show();
		System.out.println("Final states are: " + finals);
	}

	public static void main(String args[]) {
		init();
		read();
		//show();
		getEpsilonClosure();
		enfaToNfa();
	}
}
