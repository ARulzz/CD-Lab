import java.util.*;

class Solution {

	static HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>> fa;
	static HashSet<HashSet<Integer>> states;
	static HashSet<Character> inputs;
	static HashSet<Integer> start_state;
	static HashSet<HashSet<Integer>> finals;
	static HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>> eClosure;

	static void init() {
		fa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>>();
		states = new HashSet<HashSet<Integer>>();
		inputs = new HashSet<Character>();
		start_state = new HashSet<Integer>();
		finals = new HashSet<HashSet<Integer>>();
		eClosure = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
	}

	static void read() {
		Scanner sc = new Scanner(System.in);
		System.out.print("No. of transitions: ");
		int t = sc.nextInt();
		System.out.println("Transition input format:\n'<start_state> <input> <end_state>'");
		System.out.println("Epsilon input is denoted by 'E'");
		while(t-- > 0) {
			HashSet<Integer> s = new HashSet<Integer>();
			s.add(sc.nextInt());
			char i = sc.next().charAt(0);
			HashSet<Integer> e = new HashSet<Integer>();
			e.add(sc.nextInt());

			states.add(s);
			states.add(e);
			inputs.add(i);

			HashMap<Character, HashSet<HashSet<Integer>>> hm = new HashMap<Character, HashSet<HashSet<Integer>>>();
			HashSet<HashSet<Integer>> al = new HashSet<HashSet<Integer>>();
			if(fa.containsKey(s)) {
				hm = fa.get(s);
				if(hm.containsKey(i)) al = hm.get(i);
			}
			al.add(e);
			hm.put(i, al);
			fa.put(s,hm);
		}
		System.out.print("Final states (separated by a single space): ");
		while(sc.hasNext()) {
			HashSet<Integer> temp = new HashSet<Integer>();
			temp.add(sc.nextInt());
			finals.add(temp);
		}
		sc.close();
		inputs.remove(Character.valueOf('E'));
		System.out.println("\n-----End of input-----");
	}
	
	static void show() {
		String output = "";
		for(HashSet<Integer> node : fa.keySet()) {
			HashMap<Character, HashSet<HashSet<Integer>>> temp = fa.get(node);
			for(Character input : temp.keySet()) {
				//System.out.println(node + "\t" + input + "\t" + temp.get(input));
				output += (node.toString() + "\t" + input + "\t" + temp.get(input).toString()) + "\n";
			}
		}
		System.out.println(output.replace("[", "").replace("]", ""));
		System.out.println("Final states are: " + finals.toString().replace("[", "").replace("]", ""));
	}
	
	static HashSet<HashSet<Integer>> BFS(HashSet<Integer> start) {
		HashSet<HashSet<Integer>> path = new HashSet<HashSet<Integer>>();
		HashSet<HashSet<Integer>> visited = new HashSet<HashSet<Integer>>();
		Queue<HashSet<Integer>> queue = new LinkedList<HashSet<Integer>>();
		queue.add(start);
		while(!queue.isEmpty()) {
			HashSet<Integer> node = queue.poll();
			visited.add(node);
			//System.out.print(node + " ");
			path.add(node);
			try {
				HashSet<HashSet<Integer>> hs = fa.get(node).get('E');
				for(HashSet<Integer> i : hs) {
					if(!visited.contains(i)) {
						queue.add(i);
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
		eClosure = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
		for(HashSet<Integer> i : states) {
			HashSet<HashSet<Integer>> temp = new HashSet<HashSet<Integer>>();
			for(Integer j : i) {
				temp.addAll(BFS(i));
			}
			eClosure.put(i, BFS(i));
		}
		//for(HashSet<Integer> i : eClosure.keySet()) System.out.println(i.toString().replace("[", "").replace("]", "") + "\t" + eClosure.get(i).toString().replace("[", "").replace("]", ""));
	}

	static void enfaToNfa() {
		HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>> temp_fa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>>();
		for(HashSet<Integer> i : states) {
			HashSet<HashSet<Integer>> temp = eClosure.get(i);
			for(HashSet<Integer> j : temp) {
				if(finals.contains(j)) finals.add(i);
				for(Character ch : inputs) {
					if(!fa.containsKey(j) || !fa.get(j).containsKey(ch)) continue; 
					HashSet<HashSet<Integer>> temp1 = fa.get(j).get(ch);
					for(HashSet<Integer> k : temp1) {
						HashSet<HashSet<Integer>> temp2 = eClosure.get(k);
						for(HashSet<Integer> l : temp2) {
							HashMap<Character, HashSet<HashSet<Integer>>> hm = new HashMap<Character, HashSet<HashSet<Integer>>>();
							HashSet<HashSet<Integer>> al = new HashSet<HashSet<Integer>>();
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
	}

	static void nfaToDfa() {
		HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>> temp_fa = new HashMap<HashSet<Integer>, HashMap<Character, HashSet<HashSet<Integer>>>>();
		HashSet<HashSet<Integer>> temp_states = new HashSet<HashSet<Integer>>();
		Queue<HashSet<Integer>> q = new LinkedList<HashSet<Integer>>();
		q.add(start_state);
		while(!q.isEmpty()) {
			HashSet<Integer> state = q.poll();
			temp_states.add(state);
			for(Character ch : inputs) {
				HashSet<HashSet<Integer>> temp = new HashSet<HashSet<Integer>>();
				for(Integer i : state) {
					HashSet<Integer> temp1 = new HashSet<Integer>();
					temp1.add(i);
					temp.addAll(BFS(temp1));
				}
				q.addAll(temp);
			}
		}
		fa = temp_fa;
		show();
	}

	public static void main(String args[]) {
		init();
		read();
		//show();
		getEpsilonClosure();
		enfaToNfa();
		//nfaToDfa();
	}
}
