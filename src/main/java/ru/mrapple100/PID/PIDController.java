package ru.mrapple100.PID;

public class PIDController {
    private float kp;  // Коэффициент пропорциональной части
    private float ki;  // Коэффициент интегральной части
    private float kd;  // Коэффициент дифференциальной части
    public float setpoint;  // Заданное значение
    private float prevError;  // Предыдущее значение ошибки
    private float integral;  // Сумма ошибок

    public PIDController(float kp, float ki, float kd, float setpoint) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.setpoint = setpoint;
        this.prevError = 0;
        this.integral = 0;
    }

    public float compute(float currentValue) {
        float error = setpoint - currentValue;
        integral += error;
        float derivative = error - prevError;

        float output = kp * error + ki * integral + kd * derivative;

        prevError = error;
        return output;
    }

    public void updateSetpoint(float setpoint) {
        this.setpoint = setpoint;
    }

    public static void main(String[] args) {
        // Пример использования
        float kp = 0.1f;  // Коэффициент пропорциональной части
        float ki = 0.01f;  // Коэффициент интегральной части
        float kd = 0.01f;  // Коэффициент дифференциальной части
        float setpoint = 10.0f;  // Заданное значение

        PIDController controller = new PIDController(kp, ki, kd, setpoint);

        float currentValue = 0.0f;
        for (int i = 0; i < 100; i++) {
            float output = controller.compute(currentValue);
            // Примените выход регулятора к системе
            // currentValue = ваша система(currentValue, output);
        }
    }
}
