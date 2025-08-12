package fungorium;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import view.Observer;

/**
 * A Gombász egy gombáját megvalósító osztályy, felel a játékos lépéseinek
 * elvégzéséért és a pontszám tárolásáért
 */
public class Mushroom implements Player, Round, Observable {
	private GameEngine engine=GameEngine.getInstance();
	/**
	 * A játékos neve aki birtokolja a gombát
	 */
	private String name;
	/**
	 * A játekos pontszáma, aki birtokolja a gombát
	 */
	private int score;
	/**
	 * A játekos által növesztett MushBody-k listája
	 */
	private List<MushBody> mushbodies;

	private List<Observer> observers = new ArrayList<>();

	/**
	 * Konstruktor
	 * 
	 * @param n - name
	 * @param s - score
	 */
	public Mushroom(String n, int s) {
		name = n;
		score = s;
		mushbodies = new ArrayList<MushBody>();
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		for (Observer observer : observers) {
			observer.update();
		}
	}

	/**
	 * A játékos által kiválasztott Tectonra elszórja a megadott mennyiségű spórát,
	 * és a kiválasztott gombatest spórai közül pedig elveszi azokat,
	 * ezek után hogy ha a test elérte a maximálisan kiszórható spórák számát akkor
	 * el is pusztítja a testet.
	 * 
	 * @param mushBody - a MushBody amelyik kilövi a spórát
	 * @param tecton   - a tecton amire a spóra kerül
	 * @param amount   - a spora mennyiség amit ki szeretnénk lőni
	 */
	public void fireSpore(MushBody mushBody, Tecton tecton, int amount) {
		Tecton mushBodyTecton = mushBody.getLocation();
		boolean neighbors = mushBodyTecton.isNeighbor(tecton);
		if (neighbors) {
			for (MushSpore spore : mushBody.removeSpores(amount))
				tecton.addSpore(spore);
			System.out.println(
					"Firing " + amount + " spores from " + mushBody.getName() + " to tekton " + tecton.getId() + ".");
			engine.next();
		} else if (!neighbors && mushBody.isMature() && tecton.hasSameNeighbor(mushBodyTecton)) {
			for (MushSpore spore : mushBody.removeSpores(amount))
				tecton.addSpore(spore);
			System.out.println(
					"Firing " + amount + " spores from " + mushBody.getName() + " to tekton " + tecton.getId() + ".");
			engine.next();
		} else {
			System.out
					.println("Firing " + amount + " spores from " + mushBody.getName() + " to tekton " + tecton.getId()
							+ " failed because tecton " + tecton.getId() + " is not in firing range.");
		}
	}

	/**
	 * A játékos megeszik egy megbénított rovart a fonálon keresztül,
	 * ezzel a rovart elpusztítva,
	 * ezekután gomba testet növeszt, ha tud azon a Tectonon.
	 * 
	 * @param thread - a fonál amivel megeszi a rovart
	 */
	public void eatInsectWithThread(MushBody musbody, Tecton tecton) {
		Insect insect = null;
		for (Insect i : GameEngine.getInstance().getInsects()) {
			if (i.getPosition() == tecton) {
				insect = i;
			}
		}
		if (insect != null) {
			boolean edible = false;
			for (MushThread thread : tecton.getThreads()) {
				if (mushbodies.contains(thread.getMushBody()))
					edible = true;
			}
			if (edible && insect.getSpeed() == 0) {

				System.out.println("Eating " + insect.getName() + " with " + musbody.getName()
						+ " with thread from tecton " + tecton.getId() + ".");
				engine.getInsects().remove(insect);			
				//insect.die();
				String mushBodyName = name;
				MushBody newMushBody = tecton.addMushBody(mushBodyName);
				if (newMushBody != null) {
					mushbodies.add(newMushBody);
					addScore(1);
					System.out.println(
							"Growing new mushroom " + newMushBody.getName() + " on tekton " + tecton.getId() + ".");
					engine.next();
				} else
					System.out.println("Growing new mushroom Gomba1 on tekton " + tecton.getId()
							+ " failed because tekton " + tecton.getId() + " is a ZEROTECTON.");
			} else
				System.out.println(
						"Eating " + insect.getName() + " with " + musbody.getName() + " with thread from tecton "
								+ tecton.getId() + " failed because " + insect.getName() + " is not stunned.");
		} else {
			System.out.println("Eating with " + musbody.getName() + " with thread from tecton "
					+ tecton.getId() + " failed because there is no insect on " + tecton.getId() + ".");
		}
	}

	/**
	 * A megadott Tectonon egy új MushBody-t hoz létre,
	 * abban az esetben ha azon van gombafonala és megfelelő mennyiségű spóra.
	 * 
	 * @param tecton - a tecton amire a MushBody kerül
	 */
	public void growMushBody(Tecton tecton) {
		boolean mythread = false;

		for (MushThread thread : tecton.getThreads()) {
			if (mushbodies.contains(thread.getMushBody()))
				mythread = true;
		}
		for (Tecton neighbor : tecton.getNeighbors()){
			for (MushThread thread : neighbor.getThreads()) {
				if (mushbodies.contains(thread.getMushBody()))
					mythread = true;
			}
		}
		if (tecton.getSpores().size() >= 10 && mythread) {
			String mushBodyName = name;
			MushBody newMushBody = tecton.addMushBody(mushBodyName);
			if (newMushBody != null) {
				mushbodies.add(newMushBody);
				addScore(1);
				tecton.getSpores().removeAll(tecton.getSpores());
				System.out.println(
						"Growing new mushroom " + newMushBody.getName() + " on tekton " + tecton.getId());
				engine.next();
			} else {
				System.out.println(
						"Growing new mushroom " + getName() + " on tekton " + tecton.getId() + " failed because tekton "
								+ tecton.getId() + " is a ZEROTECTON");
			}
		} else {
			System.out.println("Growing new mushroom on tekton " + tecton.getId()
					+ " failed because there aren't enough spores on tekton " + tecton.getId() + ".");
		}
	}

	/**
	 * A megadott Tectonra tovább növeszti a megadott MushBody fonalát
	 * 
	 * @param tecton - tecton amire a MushThread kerül
	 * @param body   - MushBody ami a thread-et növeszti
	 */
	public void growThread(Tecton tecton, MushBody body) {
		boolean canGrow = false;
		Tecton neighbor = null;
		for (Tecton tec : tecton.getNeighbors()) {
			if (!tecton.soloSecurity(tec)) continue;
			if (tec.checkIfConnectedByThread(body.getLocation()) != -1) {
				canGrow = true;
				neighbor = tec;
			}
		}
		if (canGrow) {
			MushThread newthread = new MushThread(neighbor, tecton, body);
			boolean added = tecton.addThread(newthread);
			if (added){
				if (tecton.getSpores().size() <= 0){
					engine.next();
					System.out.println("Extending thread of " + body.getName() + " to tekton " + tecton.getId() + ".");
				}
			

				if (tecton.getSpores().size() > 0) {
					int szam = (int) (Math.random() * tecton.getNeighbors().size());
					MushThread newthread2 = new MushThread(tecton, tecton.getNeighbors().get(szam), body);
					while (!tecton.addThread(newthread2)) {
						szam = (int) (Math.random() * tecton.getNeighbors().size());
						newthread2 = new MushThread(tecton, tecton.getNeighbors().get(szam), body);
					}
					engine.next();
					System.out.println("Extending thread of " + body.getName() + " to tekton "
						+ tecton.getNeighbors().get(szam).getId() + ".");
				}
			} else
				System.out.println("Extending thread of " + body.getName() + " to tekton " + tecton.getId()
					+ " failed because tekton " + tecton.getId() + " is a SOLOTECTON and already has a thread.");
		}else {
			System.out.println("Extending thread of " + body.getName() + " to tekton " + tecton.getId()
					+ " failed because tekton " + tecton.getId() + " is not near a thread or would cause SOLOTECTON rule violation.");
			} 
	}

	/**
	 * Megnöveli a játékos pontszámát abban az esetben ha sikeresen gomba testet
	 * növeszt
	 */
	@Override
	public void addScore(int s) {

		score += s;
	}

	/**
	 * Round interface metódusának felülírása, körönként egyszer hívódik meg. Az
	 * összes MushBody-jára meghívja a round() függvényt.
	 */
	@Override
	public void round() {
		for (MushBody body : mushbodies) {
			body.round();
		}
	}

	/**
	 * Player interface metódusának felülírása, körönként egyszer hívódik meg, mikor
	 * a játékos eldönti mit lép a lehetőségei közül.
	 */
	@Override
	public void step() {
		/*
		 * Scanner scanner = new Scanner(System.in);
		 * boolean rightcommand = false;
		 * while (!rightcommand) {
		 * String input = scanner.nextLine();
		 * String[] command = input.split(" ");
		 * MushBody mushbody = null;
		 * Tecton tecton = null;
		 * int amount;
		 * switch (command[0]) {
		 * case "FIRESPORE":
		 * if (command.length != 4) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (MushBody body : mushbodies) {
		 * if (body.getName() == command[1])
		 * mushbody = body;
		 * }
		 * if (mushbody == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * 
		 * for (Tecton tec : GameEngine.getInstance().getTectons()) {
		 * if (tec.getId() == Integer.parseInt(command[2]))
		 * tecton = tec;
		 * }
		 * if (tecton == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * 
		 * if (command[3] == "-")
		 * amount = 1;
		 * else
		 * amount = Integer.parseInt(command[3]);
		 * fireSpore(mushbody, tecton, amount);
		 * rightcommand = true;
		 * break;
		 * case "GROWTHREAD":
		 * if (command.length != 3) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (MushBody body : mushbodies) {
		 * if (body.getName() == command[1])
		 * mushbody = body;
		 * }
		 * if (mushbody == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (Tecton tec : GameEngine.getInstance().getTectons()) {
		 * if (tec.getId() == Integer.parseInt(command[2]))
		 * tecton = tec;
		 * }
		 * if (tecton == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * growThread(tecton, mushbody);
		 * rightcommand = true;
		 * break;
		 * case "EATINSECT":
		 * if (command.length != 3) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (MushBody body : mushbodies) {
		 * if (body.getName() == command[1])
		 * mushbody = body;
		 * }
		 * if (mushbody == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (Tecton tec : GameEngine.getInstance().getTectons()) {
		 * if (tec.getId() == Integer.parseInt(command[2]))
		 * tecton = tec;
		 * }
		 * if (tecton == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * eatInsectWithThread(mushbody, tecton);
		 * rightcommand = true;
		 * break;
		 * case "GROWMUSHROOM":
		 * if (command.length != 2) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * for (Tecton tec : GameEngine.getInstance().getTectons()) {
		 * if (tec.getId() == Integer.parseInt(command[2]))
		 * tecton = tec;
		 * }
		 * if (tecton == null) {
		 * System.out.print("Hibás utasítást adtál meg");
		 * continue;
		 * }
		 * growMushBody(tecton);
		 * rightcommand = true;
		 * break;
		 * default:
		 * System.out.print("Hibás utasítást adtál meg");
		 * }
		 * }
		 * scanner.close();
		 **/
	}

	/**
	 * Visszatér a játékos nevével
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Beállítja a játékos nevét
	 * 
	 * @param name - the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Visszatér a játekos pontjával
	 * 
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Beállítja a játékos pontját
	 * 
	 * @param score - the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * Visszatér a játekos gombatestjeivel
	 * 
	 * @return gomabatestek listája
	 */
	public List<MushBody> getMushBodies() {
		return mushbodies;
	}

	public void addMushBody(MushBody body) {
		mushbodies.add(body);
	}

	public void removeMushBody(MushBody body) {
		mushbodies.remove(body);
	}

}
