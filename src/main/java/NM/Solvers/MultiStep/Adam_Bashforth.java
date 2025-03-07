package NM.Solvers.MultiStep;

import NM.Solvers.RungeKutta.Ralston_s2nd;
import NM.Solvers.RungeKutta.Runge_kutta3rd;
import NM.Solvers.RungeKutta.Runge_kutta4th;
import NM.Solvers.Solver;
import NM.Util.functions.Fty;

import static java.lang.Double.NaN;


public class Adam_Bashforth extends Solver {

    public double w_1 = NaN, w_2 = NaN, w_3 = NaN;

    /**
     * Constructor,
     * set starting time t = 0;
     *
     * @param f
     */
    public Adam_Bashforth(Fty<Double> f) {
        super(f);
    }

    @Override
    public void solve() {
        switch (order) {
            case 2 -> {
                this.w_1 = this.w;
                this.w = Ralston_s2nd.Ralston2ndOrderStep(this.f, this.w, this.h, this.t);
                this.t += h;
            }
            case 3 -> {
                this.w_2 = this.w_1 = this.w;
                this.w_2 = Runge_kutta3rd.RungeKutta3rdStep(this.f, this.w, this.h, this.t);
                this.t += h;
                this.w_1 = Runge_kutta3rd.RungeKutta3rdStep(this.f,  this.w_2, this.h,this.t);
                this.t += h;
                this.w   = Runge_kutta3rd.RungeKutta3rdStep(this.f,  this.w_1, this.h,this.t);
                this.t += h;
            }
            case 4 -> {
                this.w_3  = this.w_2 = this.w_1 = this.w;

                this.w_3 = Runge_kutta4th.RungeKutta4thStep(this.f, this.t, this.w, this.h);
                //first  i-3
                this.t+= h;
                this.w_2 = Runge_kutta4th.RungeKutta4thStep(this.f, this.t, this.w_3, this.h);
                //second  i-2
                this.t+= h;
                this.w_1 = Runge_kutta4th.RungeKutta4thStep(this.f, this.t, this.w_2, this.h);
                //third  i-1
                this.t+= h;
                this.w = Runge_kutta4th.RungeKutta4thStep(this.f, this.t, this.w_1, this.h);
                //fourth  i
                this.t += h;
            }
            default -> throw new RuntimeException("For Adam Bashforth order is necessary to be set, allowed ones are : [2,3,4]");
        }
        super.solve();
    }


    public static double AdamBashforth2ndStep(Fty<Double> f, double w, double w_1, double t, double t_1, double h) {
        if (PRINT_STEPS) {
            System.out.print("\nPredictor : ");
            System.out.println("§w (i+1) = w + 1/2 h  ( 3 f(t,w) - f(t-1,w-1) ))");
        }
        double ftw = f.f(t, w);
        double ftw1 = f.f(t_1, w_1);
        double w1 = w + (h / 2 * (3 * ftw - ftw1));
        if (PRINT_STEPS) {
            System.out.print("f(t,w) : ");
            System.out.print(ftw);
            System.out.print("  f(t-1,w-1)  : ");
            System.out.println(ftw1);
            System.out.print(" predict w+1 =  ");
            System.out.println(w1);
        }
        return w1;
    }

    public static double AdamBashforth3rdStep(Fty<Double> f, double w, double w_1, double w_2, double t, double h) {
        return w + h * ((23 * f.f(t, w)) - (16 * f.f(t - h, w_1)) + (5 * f.f(t - (2 * h), w_2))) / 12;
    }

    public static double AdamBashforth4thStep(Fty<Double> f, double w, double w_1, double w_2, double w_3, double t, double h) {
        return w + (h * (55 * f.f(t, w) - (59 * f.f(t - h, w_1)) + (37 * f.f(t - (2 * h), w_2)) - (9 * f.f(t - (3 * h), w_3)))) / 24;
    }

    @Override
    public void step(double w, double h) {
        switch (order) {
            case 2 -> {
                double t_1 = this.t - h;
                this.w = w + ((h / 2) * (3 * f.f(t, w) - f.f(t_1, w_1)));
            }

            case 3 ->{
                double t_1 = this.t-h;
                double t_2 = this.t-(2*h);
                this.w = w + h * ((23 * this.f.f(t, w)) - (16 * this.f.f(t_1, w_1)) + (5 * this.f.f(t_2, w_2))) / 12;
                this.w_1 = this.w;
                this.w_2 = this.w_1;
            }
            case 4 -> {
                double t_1 = this.t-h;
                double t_2 = t_1-h;
                double t_3 = t_2-h;
                this.w = w + (h * (55 * f.f(this.t, w) - (59 * f.f(t_1, w_1)) + (37 * f.f(t_2, w_2)) - (9 * f.f(t_3, w_3)))) / 24;
                //shift everything to the next i
                w_1 = w;
                w_2 = w_1;
                w_3 = w_2;
            }
        }

    }
}
